package me.syncwrld.booter.libs.reflections.util;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import me.syncwrld.booter.libs.reflections.Configuration;
import me.syncwrld.booter.libs.reflections.ReflectionsException;
import me.syncwrld.booter.libs.reflections.scanners.Scanner;
import me.syncwrld.booter.libs.reflections.scanners.Scanners;

public class ConfigurationBuilder implements Configuration {
  public static final Set<Scanner> DEFAULT_SCANNERS = new HashSet<>((Collection)Arrays.asList((Object[])new Scanners[] { Scanners.TypesAnnotated, Scanners.SubTypes }));
  
  public static final Predicate<String> DEFAULT_INPUTS_FILTER = t -> true;
  
  private Set<Scanner> scanners;
  
  private Set<URL> urls;
  
  private Predicate<String> inputsFilter;
  
  private boolean isParallel = true;
  
  private ClassLoader[] classLoaders;
  
  private boolean expandSuperTypes = true;
  
  public ConfigurationBuilder() {
    this.urls = new HashSet<>();
  }
  
  public static ConfigurationBuilder build(Object... params) {
    ConfigurationBuilder builder = new ConfigurationBuilder();
    List<Object> parameters = new ArrayList();
    for (Object param : params) {
      if (param != null)
        if (param.getClass().isArray()) {
          for (Object p : (Object[])param)
            parameters.add(p); 
        } else if (param instanceof Iterable) {
          for (Object p : param)
            parameters.add(p); 
        } else {
          parameters.add(param);
        }  
    } 
    ClassLoader[] loaders = (ClassLoader[])Stream.<Object>of(params).filter(p -> p instanceof ClassLoader).distinct().toArray(x$0 -> new ClassLoader[x$0]);
    if (loaders.length != 0)
      builder.addClassLoaders(loaders); 
    FilterBuilder inputsFilter = new FilterBuilder();
    builder.filterInputsBy(inputsFilter);
    for (Object param : parameters) {
      if (param instanceof String && !((String)param).isEmpty()) {
        builder.forPackage((String)param, loaders);
        inputsFilter.includePackage((String)param);
        continue;
      } 
      if (param instanceof Class && !Scanner.class.isAssignableFrom((Class)param)) {
        builder.addUrls(new URL[] { ClasspathHelper.forClass((Class)param, loaders) });
        inputsFilter.includePackage(((Class)param).getPackage().getName());
        continue;
      } 
      if (param instanceof URL) {
        builder.addUrls(new URL[] { (URL)param });
        continue;
      } 
      if (param instanceof Scanner) {
        builder.addScanners(new Scanner[] { (Scanner)param });
        continue;
      } 
      if (param instanceof Class && Scanner.class.isAssignableFrom((Class)param)) {
        try {
          builder.addScanners(new Scanner[] { ((Class<Scanner>)param).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]) });
        } catch (Exception e) {
          throw new RuntimeException(e);
        } 
        continue;
      } 
      if (param instanceof Predicate) {
        builder.filterInputsBy((Predicate<String>)param);
        continue;
      } 
      throw new ReflectionsException("could not use param '" + param + "'");
    } 
    if (builder.getUrls().isEmpty())
      builder.addUrls(ClasspathHelper.forClassLoader(loaders)); 
    return builder;
  }
  
  public ConfigurationBuilder forPackage(String pkg, ClassLoader... classLoaders) {
    return addUrls(ClasspathHelper.forPackage(pkg, classLoaders));
  }
  
  public ConfigurationBuilder forPackages(String... packages) {
    for (String pkg : packages)
      forPackage(pkg, new ClassLoader[0]); 
    return this;
  }
  
  public Set<Scanner> getScanners() {
    return (this.scanners != null) ? this.scanners : DEFAULT_SCANNERS;
  }
  
  public ConfigurationBuilder setScanners(Scanner... scanners) {
    this.scanners = new HashSet<>(Arrays.asList(scanners));
    return this;
  }
  
  public ConfigurationBuilder addScanners(Scanner... scanners) {
    if (this.scanners == null) {
      setScanners(scanners);
    } else {
      this.scanners.addAll(Arrays.asList(scanners));
    } 
    return this;
  }
  
  public Set<URL> getUrls() {
    return this.urls;
  }
  
  public ConfigurationBuilder setUrls(Collection<URL> urls) {
    this.urls = new HashSet<>(urls);
    return this;
  }
  
  public ConfigurationBuilder setUrls(URL... urls) {
    return setUrls(Arrays.asList(urls));
  }
  
  public ConfigurationBuilder addUrls(Collection<URL> urls) {
    this.urls.addAll(urls);
    return this;
  }
  
  public ConfigurationBuilder addUrls(URL... urls) {
    return addUrls(Arrays.asList(urls));
  }
  
  public Predicate<String> getInputsFilter() {
    return (this.inputsFilter != null) ? this.inputsFilter : DEFAULT_INPUTS_FILTER;
  }
  
  public ConfigurationBuilder setInputsFilter(Predicate<String> inputsFilter) {
    this.inputsFilter = inputsFilter;
    return this;
  }
  
  public ConfigurationBuilder filterInputsBy(Predicate<String> inputsFilter) {
    return setInputsFilter(inputsFilter);
  }
  
  public boolean isParallel() {
    return this.isParallel;
  }
  
  public ConfigurationBuilder setParallel(boolean parallel) {
    this.isParallel = parallel;
    return this;
  }
  
  public ClassLoader[] getClassLoaders() {
    return this.classLoaders;
  }
  
  public ConfigurationBuilder setClassLoaders(ClassLoader[] classLoaders) {
    this.classLoaders = classLoaders;
    return this;
  }
  
  public ConfigurationBuilder addClassLoaders(ClassLoader... classLoaders) {
    this
      .classLoaders = (this.classLoaders == null) ? classLoaders : (ClassLoader[])Stream.concat(Arrays.stream((Object[])this.classLoaders), Arrays.stream((Object[])classLoaders)).distinct().toArray(x$0 -> new ClassLoader[x$0]);
    return this;
  }
  
  public boolean shouldExpandSuperTypes() {
    return this.expandSuperTypes;
  }
  
  public ConfigurationBuilder setExpandSuperTypes(boolean expandSuperTypes) {
    this.expandSuperTypes = expandSuperTypes;
    return this;
  }
}
