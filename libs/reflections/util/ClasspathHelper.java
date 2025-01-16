package me.syncwrld.booter.libs.reflections.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import me.syncwrld.booter.libs.javax.servlet.ServletContext;
import me.syncwrld.booter.libs.reflections.Reflections;

public abstract class ClasspathHelper {
  public static ClassLoader contextClassLoader() {
    return Thread.currentThread().getContextClassLoader();
  }
  
  public static ClassLoader staticClassLoader() {
    return Reflections.class.getClassLoader();
  }
  
  public static ClassLoader[] classLoaders(ClassLoader... classLoaders) {
    if (classLoaders != null && classLoaders.length != 0)
      return classLoaders; 
    ClassLoader contextClassLoader = contextClassLoader(), staticClassLoader = staticClassLoader();
    (new ClassLoader[2])[0] = contextClassLoader;
    (new ClassLoader[2])[1] = staticClassLoader;
    (new ClassLoader[1])[0] = contextClassLoader;
    return (contextClassLoader != null) ? ((staticClassLoader != null && contextClassLoader != staticClassLoader) ? new ClassLoader[2] : new ClassLoader[1]) : new ClassLoader[0];
  }
  
  public static Collection<URL> forPackage(String name, ClassLoader... classLoaders) {
    return forResource(resourceName(name), classLoaders);
  }
  
  public static Collection<URL> forResource(String resourceName, ClassLoader... classLoaders) {
    List<URL> result = new ArrayList<>();
    ClassLoader[] loaders = classLoaders(classLoaders);
    for (ClassLoader classLoader : loaders) {
      try {
        Enumeration<URL> urls = classLoader.getResources(resourceName);
        while (urls.hasMoreElements()) {
          URL url = urls.nextElement();
          int index = url.toExternalForm().lastIndexOf(resourceName);
          if (index != -1) {
            result.add(new URL(url, url.toExternalForm().substring(0, index)));
            continue;
          } 
          result.add(url);
        } 
      } catch (IOException e) {
        if (Reflections.log != null)
          Reflections.log.severe("error getting resources for " + resourceName); 
      } 
    } 
    return distinctUrls(result);
  }
  
  public static URL forClass(Class<?> aClass, ClassLoader... classLoaders) {
    ClassLoader[] loaders = classLoaders(classLoaders);
    String resourceName = aClass.getName().replace(".", "/") + ".class";
    for (ClassLoader classLoader : loaders) {
      try {
        URL url = classLoader.getResource(resourceName);
        if (url != null) {
          String normalizedUrl = url.toExternalForm().substring(0, url.toExternalForm().lastIndexOf(aClass.getPackage().getName().replace(".", "/")));
          return new URL(normalizedUrl);
        } 
      } catch (MalformedURLException e) {
        if (Reflections.log != null)
          Reflections.log.warning("Could not get URL"); 
      } 
    } 
    return null;
  }
  
  public static Collection<URL> forClassLoader() {
    return forClassLoader(classLoaders(new ClassLoader[0]));
  }
  
  public static Collection<URL> forClassLoader(ClassLoader... classLoaders) {
    Collection<URL> result = new ArrayList<>();
    ClassLoader[] loaders = classLoaders(classLoaders);
    for (ClassLoader classLoader : loaders) {
      while (classLoader != null) {
        if (classLoader instanceof URLClassLoader) {
          URL[] urls = ((URLClassLoader)classLoader).getURLs();
          if (urls != null)
            result.addAll(Arrays.asList(urls)); 
        } 
        classLoader = classLoader.getParent();
      } 
    } 
    return distinctUrls(result);
  }
  
  public static Collection<URL> forJavaClassPath() {
    Collection<URL> urls = new ArrayList<>();
    String javaClassPath = System.getProperty("java.class.path");
    if (javaClassPath != null)
      for (String path : javaClassPath.split(File.pathSeparator)) {
        try {
          urls.add((new File(path)).toURI().toURL());
        } catch (Exception e) {
          if (Reflections.log != null)
            Reflections.log.warning("Could not get URL"); 
        } 
      }  
    return distinctUrls(urls);
  }
  
  public static Collection<URL> forWebInfLib(ServletContext servletContext) {
    Collection<URL> urls = new ArrayList<>();
    Set<?> resourcePaths = servletContext.getResourcePaths("/WEB-INF/lib");
    if (resourcePaths == null)
      return urls; 
    for (Object urlString : resourcePaths) {
      try {
        urls.add(servletContext.getResource((String)urlString));
      } catch (MalformedURLException malformedURLException) {}
    } 
    return distinctUrls(urls);
  }
  
  public static URL forWebInfClasses(ServletContext servletContext) {
    try {
      String path = servletContext.getRealPath("/WEB-INF/classes");
      if (path != null) {
        File file = new File(path);
        if (file.exists())
          return file.toURL(); 
      } else {
        return servletContext.getResource("/WEB-INF/classes");
      } 
    } catch (MalformedURLException malformedURLException) {}
    return null;
  }
  
  public static Collection<URL> forManifest() {
    return forManifest(forClassLoader());
  }
  
  public static Collection<URL> forManifest(URL url) {
    Collection<URL> result = new ArrayList<>();
    result.add(url);
    try {
      String part = cleanPath(url);
      File jarFile = new File(part);
      JarFile myJar = new JarFile(part);
      URL validUrl = tryToGetValidUrl(jarFile.getPath(), (new File(part)).getParent(), part);
      if (validUrl != null)
        result.add(validUrl); 
      Manifest manifest = myJar.getManifest();
      if (manifest != null) {
        String classPath = manifest.getMainAttributes().getValue(new Attributes.Name("Class-Path"));
        if (classPath != null)
          for (String jar : classPath.split(" ")) {
            validUrl = tryToGetValidUrl(jarFile.getPath(), (new File(part)).getParent(), jar);
            if (validUrl != null)
              result.add(validUrl); 
          }  
      } 
    } catch (IOException iOException) {}
    return distinctUrls(result);
  }
  
  public static Collection<URL> forManifest(Iterable<URL> urls) {
    Collection<URL> result = new ArrayList<>();
    for (URL url : urls)
      result.addAll(forManifest(url)); 
    return distinctUrls(result);
  }
  
  static URL tryToGetValidUrl(String workingDir, String path, String filename) {
    try {
      if ((new File(filename)).exists())
        return (new File(filename)).toURI().toURL(); 
      if ((new File(path + File.separator + filename)).exists())
        return (new File(path + File.separator + filename)).toURI().toURL(); 
      if ((new File(workingDir + File.separator + filename)).exists())
        return (new File(workingDir + File.separator + filename)).toURI().toURL(); 
      if ((new File((new URL(filename)).getFile())).exists())
        return (new File((new URL(filename)).getFile())).toURI().toURL(); 
    } catch (MalformedURLException malformedURLException) {}
    return null;
  }
  
  public static String cleanPath(URL url) {
    String path = url.getPath();
    try {
      path = URLDecoder.decode(path, "UTF-8");
    } catch (UnsupportedEncodingException unsupportedEncodingException) {}
    if (path.startsWith("jar:"))
      path = path.substring("jar:".length()); 
    if (path.startsWith("file:"))
      path = path.substring("file:".length()); 
    if (path.endsWith("!/"))
      path = path.substring(0, path.lastIndexOf("!/")) + "/"; 
    return path;
  }
  
  private static String resourceName(String name) {
    if (name != null) {
      String resourceName = name.replace(".", "/");
      resourceName = resourceName.replace("\\", "/");
      if (resourceName.startsWith("/"))
        resourceName = resourceName.substring(1); 
      return resourceName;
    } 
    return null;
  }
  
  private static Collection<URL> distinctUrls(Collection<URL> urls) {
    Map<String, URL> distinct = new LinkedHashMap<>(urls.size());
    for (URL url : urls)
      distinct.put(url.toExternalForm(), url); 
    return distinct.values();
  }
}
