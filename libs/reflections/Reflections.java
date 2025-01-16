package me.syncwrld.booter.libs.reflections;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import me.syncwrld.booter.libs.javassist.bytecode.ClassFile;
import me.syncwrld.booter.libs.javax.annotation.Nullable;
import me.syncwrld.booter.libs.reflections.scanners.MemberUsageScanner;
import me.syncwrld.booter.libs.reflections.scanners.MethodParameterNamesScanner;
import me.syncwrld.booter.libs.reflections.scanners.Scanner;
import me.syncwrld.booter.libs.reflections.scanners.Scanners;
import me.syncwrld.booter.libs.reflections.serializers.Serializer;
import me.syncwrld.booter.libs.reflections.serializers.XmlSerializer;
import me.syncwrld.booter.libs.reflections.util.ClasspathHelper;
import me.syncwrld.booter.libs.reflections.util.ConfigurationBuilder;
import me.syncwrld.booter.libs.reflections.util.FilterBuilder;
import me.syncwrld.booter.libs.reflections.util.NameHelper;
import me.syncwrld.booter.libs.reflections.util.QueryFunction;
import me.syncwrld.booter.libs.reflections.vfs.Vfs;

public class Reflections implements NameHelper {
  public static final Logger log = Logger.getLogger(Reflections.class.getName());
  
  protected final transient Configuration configuration;
  
  protected final Store store;
  
  public Reflections(Configuration configuration) {
    this.configuration = configuration;
    Map<String, Map<String, Set<String>>> storeMap = scan();
    if (configuration.shouldExpandSuperTypes())
      expandSuperTypes(storeMap.get(Scanners.SubTypes.index()), storeMap.get(Scanners.TypesAnnotated.index())); 
    this.store = new Store(storeMap);
    configLogger();
  }
  
  public Reflections(Store store) {
    this.configuration = (Configuration)new ConfigurationBuilder();
    this.store = store;
    configLogger();
  }
  
  public Reflections(String prefix, Scanner... scanners) {
    this(new Object[] { prefix, scanners });
    configLogger();
  }
  
  public Reflections(Object... params) {
    this((Configuration)ConfigurationBuilder.build(params));
    configLogger();
  }
  
  protected Reflections() {
    this.configuration = (Configuration)new ConfigurationBuilder();
    this.store = new Store(new HashMap<>());
    configLogger();
  }
  
  public static Reflections collect() {
    return collect("META-INF/reflections/", (Predicate<String>)(new FilterBuilder())
        .includePattern(".*-reflections\\.xml"));
  }
  
  public static void configLogger() {
    log.setUseParentHandlers(false);
    for (Handler handler : log.getHandlers())
      log.removeHandler(handler); 
    Handler consoleHandler = new ConsoleHandler();
    consoleHandler.setFormatter(new SimpleFormatter() {
          public String format(LogRecord record) {
            return "[Reflections] " + record.getMessage() + "\n";
          }
        });
    log.addHandler(consoleHandler);
  }
  
  public static Reflections collect(String packagePrefix, Predicate<String> resourceNameFilter) {
    return collect(packagePrefix, resourceNameFilter, (Serializer)new XmlSerializer());
  }
  
  public static Reflections collect(String packagePrefix, Predicate<String> resourceNameFilter, Serializer serializer) {
    Collection<URL> urls = ClasspathHelper.forPackage(packagePrefix, new ClassLoader[0]);
    Iterable<Vfs.File> files = Vfs.findFiles(urls, packagePrefix, resourceNameFilter);
    Reflections reflections = new Reflections();
    StreamSupport.stream(files.spliterator(), false)
      .forEach(file -> {
          try (InputStream inputStream = file.openInputStream()) {
            reflections.collect(inputStream, serializer);
          } catch (IOException e) {
            throw new ReflectionsException("could not merge " + file, e);
          } 
        });
    return reflections;
  }
  
  protected Map<String, Map<String, Set<String>>> scan() {
    long start = System.currentTimeMillis();
    Map<String, Set<Map.Entry<String, String>>> collect = (Map<String, Set<Map.Entry<String, String>>>)this.configuration.getScanners().stream().map(Scanner::index).distinct().collect(Collectors.toMap(s -> s, s -> Collections.synchronizedSet(new HashSet())));
    Set<URL> urls = this.configuration.getUrls();
    (this.configuration.isParallel() ? urls.stream().parallel() : (Stream)urls.stream())
      .forEach(url -> {
          try (Vfs.Dir dir = Vfs.fromURL(url)) {
            for (Vfs.File file : dir.getFiles()) {
              if (doFilter(file, this.configuration.getInputsFilter())) {
                ClassFile classFile = null;
                for (Scanner scanner : this.configuration.getScanners()) {
                  try {
                    if (doFilter(file, scanner::acceptsInput)) {
                      List<Map.Entry<String, String>> entries = scanner.scan(file);
                      if (entries == null) {
                        if (classFile == null)
                          classFile = getClassFile(file); 
                        entries = scanner.scan(classFile);
                      } 
                      if (entries != null)
                        ((Set<Map.Entry<String, String>>)collect.get(scanner.index())).addAll(entries); 
                    } 
                  } catch (Exception e) {
                    if (log != null)
                      log.warning("could not scan file " + file.getRelativePath() + ": " + e); 
                  } 
                } 
              } 
            } 
          } catch (Exception e) {
            if (log != null)
              log.warning("could not create Vfs.Dir from url. ignoring the exception and continuing: url -> " + url + ", exception -> " + e); 
          } 
        });
    Map<String, Map<String, Set<String>>> storeMap = (Map<String, Map<String, Set<String>>>)collect.entrySet().stream().collect(
        Collectors.toMap(Map.Entry::getKey, entry -> (HashMap)((Set)entry.getValue()).stream().filter(()).collect(Collectors.groupingBy(Map.Entry::getKey, HashMap::new, Collectors.mapping(Map.Entry::getValue, Collectors.toSet())))));
    if (log != null) {
      int keys = 0, values = 0;
      for (Map<String, Set<String>> map : storeMap.values()) {
        keys += map.size();
        values = (int)(values + map.values().stream().mapToLong(Set::size).sum());
      } 
    } 
    return storeMap;
  }
  
  private boolean doFilter(Vfs.File file, @Nullable Predicate<String> predicate) {
    String path = file.getRelativePath();
    String fqn = path.replace('/', '.');
    return (predicate == null || predicate.test(path) || predicate.test(fqn));
  }
  
  private ClassFile getClassFile(Vfs.File file) {
    try (DataInputStream dis = new DataInputStream(new BufferedInputStream(file
            .openInputStream()))) {
      return new ClassFile(dis);
    } catch (Exception e) {
      throw new ReflectionsException("could not create class object from file " + file
          .getRelativePath(), e);
    } 
  }
  
  public Reflections collect(InputStream inputStream, Serializer serializer) {
    return merge(serializer.read(inputStream));
  }
  
  public Reflections collect(File file, Serializer serializer) {
    try (FileInputStream inputStream = new FileInputStream(file)) {
      return collect(inputStream, serializer);
    } catch (IOException e) {
      throw new ReflectionsException("could not obtain input stream from file " + file, e);
    } 
  }
  
  public Reflections merge(Reflections reflections) {
    reflections.store.forEach((index, map) -> this.store.merge(index, map, ()));
    return this;
  }
  
  public void expandSuperTypes(Map<String, Set<String>> subTypesStore, Map<String, Set<String>> typesAnnotatedStore) {
    if (subTypesStore == null || subTypesStore.isEmpty())
      return; 
    Set<String> keys = new LinkedHashSet<>(subTypesStore.keySet());
    keys.removeAll((Collection)subTypesStore
        .values().stream().flatMap(Collection::stream).collect(Collectors.toSet()));
    keys.remove("java.lang.Object");
    for (String key : keys) {
      Class<?> type = forClass(key, loaders());
      if (type != null)
        expandSupertypes(subTypesStore, typesAnnotatedStore, key, type); 
    } 
  }
  
  private void expandSupertypes(Map<String, Set<String>> subTypesStore, Map<String, Set<String>> typesAnnotatedStore, String key, Class<?> type) {
    Set<Annotation> typeAnnotations = ReflectionUtils.getAnnotations(type, (Predicate<Annotation>[])new Predicate[0]);
    if (typesAnnotatedStore != null && !typeAnnotations.isEmpty()) {
      String typeName = type.getName();
      for (Annotation typeAnnotation : typeAnnotations) {
        String annotationName = typeAnnotation.annotationType().getName();
        ((Set<String>)typesAnnotatedStore.computeIfAbsent(annotationName, s -> new HashSet())).add(typeName);
      } 
    } 
    for (Class<?> supertype : ReflectionUtils.getSuperTypes(type)) {
      String supertypeName = supertype.getName();
      if (subTypesStore.containsKey(supertypeName)) {
        ((Set<String>)subTypesStore.get(supertypeName)).add(key);
        continue;
      } 
      ((Set<String>)subTypesStore.computeIfAbsent(supertypeName, s -> new HashSet())).add(key);
      expandSupertypes(subTypesStore, typesAnnotatedStore, supertypeName, supertype);
    } 
  }
  
  public <T> Set<T> get(QueryFunction<Store, T> query) {
    return query.apply(this.store);
  }
  
  public <T> Set<Class<? extends T>> getSubTypesOf(Class<T> type) {
    return 
      get(Scanners.SubTypes.of(new AnnotatedElement[] { type }).as(Class.class, loaders()));
  }
  
  public Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation> annotation) {
    return get(Scanners.SubTypes.of(Scanners.TypesAnnotated.with(new AnnotatedElement[] { annotation })).asClass(loaders()));
  }
  
  public Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation> annotation, boolean honorInherited) {
    if (!honorInherited)
      return getTypesAnnotatedWith(annotation); 
    if (annotation.isAnnotationPresent((Class)Inherited.class))
      return get(Scanners.TypesAnnotated
          .get(annotation)
          .add(Scanners.SubTypes
            .of(Scanners.TypesAnnotated
              .get(annotation)
              .filter(c -> !forClass(c, loaders()).isInterface())))
          .asClass(loaders())); 
    return get(Scanners.TypesAnnotated.get(annotation).asClass(loaders()));
  }
  
  public Set<Class<?>> getTypesAnnotatedWith(Annotation annotation) {
    return get(Scanners.SubTypes
        .of(Scanners.TypesAnnotated
          .of(Scanners.TypesAnnotated
            .get(annotation.annotationType())
            .filter(c -> ReflectionUtils.withAnnotation(annotation).test(forClass(c, loaders())))))
        .asClass(loaders()));
  }
  
  public Set<Class<?>> getTypesAnnotatedWith(Annotation annotation, boolean honorInherited) {
    if (!honorInherited)
      return getTypesAnnotatedWith(annotation); 
    Class<? extends Annotation> type = annotation.annotationType();
    if (type.isAnnotationPresent((Class)Inherited.class))
      return get(Scanners.TypesAnnotated
          .with(new AnnotatedElement[] { type }).asClass(loaders())
          .filter(ReflectionUtils.withAnnotation(annotation))
          .add(Scanners.SubTypes
            .of(Scanners.TypesAnnotated
              .with(new AnnotatedElement[] { type }).asClass(loaders())
              .filter(c -> !c.isInterface())))); 
    return get(Scanners.TypesAnnotated.with(new AnnotatedElement[] { type }).asClass(loaders()).filter(ReflectionUtils.withAnnotation(annotation)));
  }
  
  public Set<Method> getMethodsAnnotatedWith(Class<? extends Annotation> annotation) {
    return get(Scanners.MethodsAnnotated.with(new AnnotatedElement[] { annotation }).as(Method.class, loaders()));
  }
  
  public Set<Method> getMethodsAnnotatedWith(Annotation annotation) {
    return get(Scanners.MethodsAnnotated
        .with(new AnnotatedElement[] { annotation.annotationType() }).as(Method.class, loaders())
        .filter(ReflectionUtils.withAnnotation(annotation)));
  }
  
  public Set<Method> getMethodsWithSignature(Class<?>... types) {
    return get(Scanners.MethodsSignature.with((AnnotatedElement[])types).as(Method.class, loaders()));
  }
  
  public Set<Method> getMethodsWithParameter(AnnotatedElement type) {
    return get(Scanners.MethodsParameter.with(new AnnotatedElement[] { type }).as(Method.class, loaders()));
  }
  
  public Set<Method> getMethodsReturn(Class<?> type) {
    return get(Scanners.MethodsReturn.of(new AnnotatedElement[] { type }).as(Method.class, loaders()));
  }
  
  public Set<Constructor> getConstructorsAnnotatedWith(Class<? extends Annotation> annotation) {
    return get(Scanners.ConstructorsAnnotated.with(new AnnotatedElement[] { annotation }).as(Constructor.class, loaders()));
  }
  
  public Set<Constructor> getConstructorsAnnotatedWith(Annotation annotation) {
    return get(Scanners.ConstructorsAnnotated
        .with(new AnnotatedElement[] { annotation.annotationType() }).as(Constructor.class, loaders())
        .filter(ReflectionUtils.withAnyParameterAnnotation(annotation)));
  }
  
  public Set<Constructor> getConstructorsWithSignature(Class<?>... types) {
    return get(Scanners.ConstructorsSignature.with((AnnotatedElement[])types).as(Constructor.class, loaders()));
  }
  
  public Set<Constructor> getConstructorsWithParameter(AnnotatedElement type) {
    return get(Scanners.ConstructorsParameter.of(new AnnotatedElement[] { type }).as(Constructor.class, loaders()));
  }
  
  public Set<Field> getFieldsAnnotatedWith(Class<? extends Annotation> annotation) {
    return get(Scanners.FieldsAnnotated.with(new AnnotatedElement[] { annotation }).as(Field.class, loaders()));
  }
  
  public Set<Field> getFieldsAnnotatedWith(Annotation annotation) {
    return get(Scanners.FieldsAnnotated
        .with(new AnnotatedElement[] { annotation.annotationType() }).as(Field.class, loaders())
        .filter(ReflectionUtils.withAnnotation(annotation)));
  }
  
  public Set<String> getResources(String pattern) {
    return get(Scanners.Resources.with(pattern));
  }
  
  public Set<String> getResources(Pattern pattern) {
    return getResources(pattern.pattern());
  }
  
  public List<String> getMemberParameterNames(Member member) {
    return (List<String>)((Set)this.store
      .getOrDefault(MethodParameterNamesScanner.class.getSimpleName(), Collections.emptyMap())
      .getOrDefault(toName((AnnotatedElement)member), Collections.emptySet()))
      .stream()
      .flatMap(s -> Stream.of(s.split(", ")))
      .collect(Collectors.toList());
  }
  
  public Collection<Member> getMemberUsage(Member member) {
    Set<String> usages = this.store.getOrDefault(MemberUsageScanner.class.getSimpleName(), Collections.emptyMap()).getOrDefault(toName((AnnotatedElement)member), Collections.emptySet());
    return forNames(usages, Member.class, loaders());
  }
  
  @Deprecated
  public Set<String> getAllTypes() {
    return getAll((Scanner)Scanners.SubTypes);
  }
  
  public Set<String> getAll(Scanner scanner) {
    Map<String, Set<String>> map = this.store.getOrDefault(scanner.index(), Collections.emptyMap());
    return (Set<String>)Stream.concat(map.keySet().stream(), map.values().stream().flatMap(Collection::stream))
      .collect(Collectors.toCollection(LinkedHashSet::new));
  }
  
  public Store getStore() {
    return this.store;
  }
  
  public Configuration getConfiguration() {
    return this.configuration;
  }
  
  public File save(String filename) {
    return save(filename, (Serializer)new XmlSerializer());
  }
  
  public File save(String filename, Serializer serializer) {
    return serializer.save(this, filename);
  }
  
  ClassLoader[] loaders() {
    return this.configuration.getClassLoaders();
  }
}
