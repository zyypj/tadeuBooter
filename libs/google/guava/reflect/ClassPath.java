package me.syncwrld.booter.libs.google.guava.reflect;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.syncwrld.booter.libs.google.guava.annotations.VisibleForTesting;
import me.syncwrld.booter.libs.google.guava.base.CharMatcher;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.guava.base.Splitter;
import me.syncwrld.booter.libs.google.guava.base.StandardSystemProperty;
import me.syncwrld.booter.libs.google.guava.collect.FluentIterable;
import me.syncwrld.booter.libs.google.guava.collect.ImmutableList;
import me.syncwrld.booter.libs.google.guava.collect.ImmutableMap;
import me.syncwrld.booter.libs.google.guava.collect.ImmutableSet;
import me.syncwrld.booter.libs.google.guava.collect.Maps;
import me.syncwrld.booter.libs.google.guava.collect.UnmodifiableIterator;
import me.syncwrld.booter.libs.google.guava.io.ByteSource;
import me.syncwrld.booter.libs.google.guava.io.CharSource;
import me.syncwrld.booter.libs.google.guava.io.Resources;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
public final class ClassPath {
  private static final Logger logger = Logger.getLogger(ClassPath.class.getName());
  
  private static final Splitter CLASS_PATH_ATTRIBUTE_SEPARATOR = Splitter.on(" ").omitEmptyStrings();
  
  private static final String CLASS_FILE_NAME_EXTENSION = ".class";
  
  private final ImmutableSet<ResourceInfo> resources;
  
  private ClassPath(ImmutableSet<ResourceInfo> resources) {
    this.resources = resources;
  }
  
  public static ClassPath from(ClassLoader classloader) throws IOException {
    ImmutableSet<LocationInfo> locations = locationsFrom(classloader);
    Set<File> scanned = new HashSet<>();
    for (UnmodifiableIterator<LocationInfo> unmodifiableIterator1 = locations.iterator(); unmodifiableIterator1.hasNext(); ) {
      LocationInfo location = unmodifiableIterator1.next();
      scanned.add(location.file());
    } 
    ImmutableSet.Builder<ResourceInfo> builder = ImmutableSet.builder();
    for (UnmodifiableIterator<LocationInfo> unmodifiableIterator2 = locations.iterator(); unmodifiableIterator2.hasNext(); ) {
      LocationInfo location = unmodifiableIterator2.next();
      builder.addAll((Iterable)location.scanResources(scanned));
    } 
    return new ClassPath(builder.build());
  }
  
  public ImmutableSet<ResourceInfo> getResources() {
    return this.resources;
  }
  
  public ImmutableSet<ClassInfo> getAllClasses() {
    return FluentIterable.from((Iterable)this.resources).filter(ClassInfo.class).toSet();
  }
  
  public ImmutableSet<ClassInfo> getTopLevelClasses() {
    return FluentIterable.from((Iterable)this.resources)
      .filter(ClassInfo.class)
      .filter(ClassInfo::isTopLevel)
      .toSet();
  }
  
  public ImmutableSet<ClassInfo> getTopLevelClasses(String packageName) {
    Preconditions.checkNotNull(packageName);
    ImmutableSet.Builder<ClassInfo> builder = ImmutableSet.builder();
    for (UnmodifiableIterator<ClassInfo> unmodifiableIterator = getTopLevelClasses().iterator(); unmodifiableIterator.hasNext(); ) {
      ClassInfo classInfo = unmodifiableIterator.next();
      if (classInfo.getPackageName().equals(packageName))
        builder.add(classInfo); 
    } 
    return builder.build();
  }
  
  public ImmutableSet<ClassInfo> getTopLevelClassesRecursive(String packageName) {
    Preconditions.checkNotNull(packageName);
    String packagePrefix = packageName + '.';
    ImmutableSet.Builder<ClassInfo> builder = ImmutableSet.builder();
    for (UnmodifiableIterator<ClassInfo> unmodifiableIterator = getTopLevelClasses().iterator(); unmodifiableIterator.hasNext(); ) {
      ClassInfo classInfo = unmodifiableIterator.next();
      if (classInfo.getName().startsWith(packagePrefix))
        builder.add(classInfo); 
    } 
    return builder.build();
  }
  
  public static class ResourceInfo {
    private final File file;
    
    private final String resourceName;
    
    final ClassLoader loader;
    
    static ResourceInfo of(File file, String resourceName, ClassLoader loader) {
      if (resourceName.endsWith(".class"))
        return new ClassPath.ClassInfo(file, resourceName, loader); 
      return new ResourceInfo(file, resourceName, loader);
    }
    
    ResourceInfo(File file, String resourceName, ClassLoader loader) {
      this.file = (File)Preconditions.checkNotNull(file);
      this.resourceName = (String)Preconditions.checkNotNull(resourceName);
      this.loader = (ClassLoader)Preconditions.checkNotNull(loader);
    }
    
    public final URL url() {
      URL url = this.loader.getResource(this.resourceName);
      if (url == null)
        throw new NoSuchElementException(this.resourceName); 
      return url;
    }
    
    public final ByteSource asByteSource() {
      return Resources.asByteSource(url());
    }
    
    public final CharSource asCharSource(Charset charset) {
      return Resources.asCharSource(url(), charset);
    }
    
    public final String getResourceName() {
      return this.resourceName;
    }
    
    final File getFile() {
      return this.file;
    }
    
    public int hashCode() {
      return this.resourceName.hashCode();
    }
    
    public boolean equals(@CheckForNull Object obj) {
      if (obj instanceof ResourceInfo) {
        ResourceInfo that = (ResourceInfo)obj;
        return (this.resourceName.equals(that.resourceName) && this.loader == that.loader);
      } 
      return false;
    }
    
    public String toString() {
      return this.resourceName;
    }
  }
  
  public static final class ClassInfo extends ResourceInfo {
    private final String className;
    
    ClassInfo(File file, String resourceName, ClassLoader loader) {
      super(file, resourceName, loader);
      this.className = ClassPath.getClassName(resourceName);
    }
    
    public String getPackageName() {
      return Reflection.getPackageName(this.className);
    }
    
    public String getSimpleName() {
      int lastDollarSign = this.className.lastIndexOf('$');
      if (lastDollarSign != -1) {
        String innerClassName = this.className.substring(lastDollarSign + 1);
        return CharMatcher.inRange('0', '9').trimLeadingFrom(innerClassName);
      } 
      String packageName = getPackageName();
      if (packageName.isEmpty())
        return this.className; 
      return this.className.substring(packageName.length() + 1);
    }
    
    public String getName() {
      return this.className;
    }
    
    public boolean isTopLevel() {
      return (this.className.indexOf('$') == -1);
    }
    
    public Class<?> load() {
      try {
        return this.loader.loadClass(this.className);
      } catch (ClassNotFoundException e) {
        throw new IllegalStateException(e);
      } 
    }
    
    public String toString() {
      return this.className;
    }
  }
  
  static ImmutableSet<LocationInfo> locationsFrom(ClassLoader classloader) {
    ImmutableSet.Builder<LocationInfo> builder = ImmutableSet.builder();
    for (UnmodifiableIterator<Map.Entry<File, ClassLoader>> unmodifiableIterator = getClassPathEntries(classloader).entrySet().iterator(); unmodifiableIterator.hasNext(); ) {
      Map.Entry<File, ClassLoader> entry = unmodifiableIterator.next();
      builder.add(new LocationInfo(entry.getKey(), entry.getValue()));
    } 
    return builder.build();
  }
  
  static final class LocationInfo {
    final File home;
    
    private final ClassLoader classloader;
    
    LocationInfo(File home, ClassLoader classloader) {
      this.home = (File)Preconditions.checkNotNull(home);
      this.classloader = (ClassLoader)Preconditions.checkNotNull(classloader);
    }
    
    public final File file() {
      return this.home;
    }
    
    public ImmutableSet<ClassPath.ResourceInfo> scanResources() throws IOException {
      return scanResources(new HashSet<>());
    }
    
    public ImmutableSet<ClassPath.ResourceInfo> scanResources(Set<File> scannedFiles) throws IOException {
      ImmutableSet.Builder<ClassPath.ResourceInfo> builder = ImmutableSet.builder();
      scannedFiles.add(this.home);
      scan(this.home, scannedFiles, builder);
      return builder.build();
    }
    
    private void scan(File file, Set<File> scannedUris, ImmutableSet.Builder<ClassPath.ResourceInfo> builder) throws IOException {
      try {
        if (!file.exists())
          return; 
      } catch (SecurityException e) {
        ClassPath.logger.warning("Cannot access " + file + ": " + e);
        return;
      } 
      if (file.isDirectory()) {
        scanDirectory(file, builder);
      } else {
        scanJar(file, scannedUris, builder);
      } 
    }
    
    private void scanJar(File file, Set<File> scannedUris, ImmutableSet.Builder<ClassPath.ResourceInfo> builder) throws IOException {
      JarFile jarFile;
      try {
        jarFile = new JarFile(file);
      } catch (IOException e) {
        return;
      } 
      try {
        for (UnmodifiableIterator<File> unmodifiableIterator = ClassPath.getClassPathFromManifest(file, jarFile.getManifest()).iterator(); unmodifiableIterator.hasNext(); ) {
          File path = unmodifiableIterator.next();
          if (scannedUris.add(path.getCanonicalFile()))
            scan(path, scannedUris, builder); 
        } 
        scanJarFile(jarFile, builder);
      } finally {
        try {
          jarFile.close();
        } catch (IOException iOException) {}
      } 
    }
    
    private void scanJarFile(JarFile file, ImmutableSet.Builder<ClassPath.ResourceInfo> builder) {
      Enumeration<JarEntry> entries = file.entries();
      while (entries.hasMoreElements()) {
        JarEntry entry = entries.nextElement();
        if (entry.isDirectory() || entry.getName().equals("META-INF/MANIFEST.MF"))
          continue; 
        builder.add(ClassPath.ResourceInfo.of(new File(file.getName()), entry.getName(), this.classloader));
      } 
    }
    
    private void scanDirectory(File directory, ImmutableSet.Builder<ClassPath.ResourceInfo> builder) throws IOException {
      Set<File> currentPath = new HashSet<>();
      currentPath.add(directory.getCanonicalFile());
      scanDirectory(directory, "", currentPath, builder);
    }
    
    private void scanDirectory(File directory, String packagePrefix, Set<File> currentPath, ImmutableSet.Builder<ClassPath.ResourceInfo> builder) throws IOException {
      File[] files = directory.listFiles();
      if (files == null) {
        ClassPath.logger.warning("Cannot read directory " + directory);
        return;
      } 
      for (File f : files) {
        String name = f.getName();
        if (f.isDirectory()) {
          File deref = f.getCanonicalFile();
          if (currentPath.add(deref)) {
            scanDirectory(deref, packagePrefix + name + "/", currentPath, builder);
            currentPath.remove(deref);
          } 
        } else {
          String resourceName = packagePrefix + name;
          if (!resourceName.equals("META-INF/MANIFEST.MF"))
            builder.add(ClassPath.ResourceInfo.of(f, resourceName, this.classloader)); 
        } 
      } 
    }
    
    public boolean equals(@CheckForNull Object obj) {
      if (obj instanceof LocationInfo) {
        LocationInfo that = (LocationInfo)obj;
        return (this.home.equals(that.home) && this.classloader.equals(that.classloader));
      } 
      return false;
    }
    
    public int hashCode() {
      return this.home.hashCode();
    }
    
    public String toString() {
      return this.home.toString();
    }
  }
  
  @VisibleForTesting
  static ImmutableSet<File> getClassPathFromManifest(File jarFile, @CheckForNull Manifest manifest) {
    if (manifest == null)
      return ImmutableSet.of(); 
    ImmutableSet.Builder<File> builder = ImmutableSet.builder();
    String classpathAttribute = manifest.getMainAttributes().getValue(Attributes.Name.CLASS_PATH.toString());
    if (classpathAttribute != null)
      for (String path : CLASS_PATH_ATTRIBUTE_SEPARATOR.split(classpathAttribute)) {
        URL url;
        try {
          url = getClassPathEntry(jarFile, path);
        } catch (MalformedURLException e) {
          logger.warning("Invalid Class-Path entry: " + path);
          continue;
        } 
        if (url.getProtocol().equals("file"))
          builder.add(toFile(url)); 
      }  
    return builder.build();
  }
  
  @VisibleForTesting
  static ImmutableMap<File, ClassLoader> getClassPathEntries(ClassLoader classloader) {
    LinkedHashMap<File, ClassLoader> entries = Maps.newLinkedHashMap();
    ClassLoader parent = classloader.getParent();
    if (parent != null)
      entries.putAll((Map<? extends File, ? extends ClassLoader>)getClassPathEntries(parent)); 
    for (UnmodifiableIterator<URL> unmodifiableIterator = getClassLoaderUrls(classloader).iterator(); unmodifiableIterator.hasNext(); ) {
      URL url = unmodifiableIterator.next();
      if (url.getProtocol().equals("file")) {
        File file = toFile(url);
        if (!entries.containsKey(file))
          entries.put(file, classloader); 
      } 
    } 
    return ImmutableMap.copyOf(entries);
  }
  
  private static ImmutableList<URL> getClassLoaderUrls(ClassLoader classloader) {
    if (classloader instanceof URLClassLoader)
      return ImmutableList.copyOf((Object[])((URLClassLoader)classloader).getURLs()); 
    if (classloader.equals(ClassLoader.getSystemClassLoader()))
      return parseJavaClassPath(); 
    return ImmutableList.of();
  }
  
  @VisibleForTesting
  static ImmutableList<URL> parseJavaClassPath() {
    ImmutableList.Builder<URL> urls = ImmutableList.builder();
    for (String entry : Splitter.on(StandardSystemProperty.PATH_SEPARATOR.value()).split(StandardSystemProperty.JAVA_CLASS_PATH.value())) {
      try {
        try {
          urls.add((new File(entry)).toURI().toURL());
        } catch (SecurityException e) {
          urls.add(new URL("file", null, (new File(entry)).getAbsolutePath()));
        } 
      } catch (MalformedURLException e) {
        logger.log(Level.WARNING, "malformed classpath entry: " + entry, e);
      } 
    } 
    return urls.build();
  }
  
  @VisibleForTesting
  static URL getClassPathEntry(File jarFile, String path) throws MalformedURLException {
    return new URL(jarFile.toURI().toURL(), path);
  }
  
  @VisibleForTesting
  static String getClassName(String filename) {
    int classNameEnd = filename.length() - ".class".length();
    return filename.substring(0, classNameEnd).replace('/', '.');
  }
  
  @VisibleForTesting
  static File toFile(URL url) {
    Preconditions.checkArgument(url.getProtocol().equals("file"));
    try {
      return new File(url.toURI());
    } catch (URISyntaxException e) {
      return new File(url.getPath());
    } 
  }
}
