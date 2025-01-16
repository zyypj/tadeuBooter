package me.syncwrld.booter.libs.reflections.vfs;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.jar.JarFile;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import me.syncwrld.booter.libs.reflections.Reflections;
import me.syncwrld.booter.libs.reflections.ReflectionsException;
import me.syncwrld.booter.libs.reflections.util.ClasspathHelper;

public abstract class Vfs {
  private static List<UrlType> defaultUrlTypes = new ArrayList<>(
      Arrays.asList((UrlType[])DefaultUrlTypes.values()));
  
  public static List<UrlType> getDefaultUrlTypes() {
    return defaultUrlTypes;
  }
  
  public static void setDefaultURLTypes(List<UrlType> urlTypes) {
    defaultUrlTypes = urlTypes;
  }
  
  public static void addDefaultURLTypes(UrlType urlType) {
    defaultUrlTypes.add(0, urlType);
  }
  
  public static Dir fromURL(URL url) {
    return fromURL(url, defaultUrlTypes);
  }
  
  public static Dir fromURL(URL url, List<UrlType> urlTypes) {
    for (UrlType type : urlTypes) {
      try {
        if (type.matches(url)) {
          Dir dir = type.createDir(url);
          if (dir != null)
            return dir; 
        } 
      } catch (Throwable e) {
        if (Reflections.log != null)
          Reflections.log.warning("could not create Dir using " + type + " from url " + url
              
              .toExternalForm() + ". skipping. [" + e
              
              .getMessage() + "]"); 
      } 
    } 
    throw new ReflectionsException("could not create Vfs.Dir from url, no matching UrlType was found [" + url
        
        .toExternalForm() + "]\neither use fromURL(final URL url, final List<UrlType> urlTypes) or use the static setDefaultURLTypes(final List<UrlType> urlTypes) or addDefaultURLTypes(UrlType urlType) with your specialized UrlType.");
  }
  
  public static Dir fromURL(URL url, UrlType... urlTypes) {
    return fromURL(url, Arrays.asList(urlTypes));
  }
  
  public static Iterable<File> findFiles(Collection<URL> inUrls, String packagePrefix, Predicate<String> nameFilter) {
    Predicate<File> fileNamePredicate = file -> {
        String path = file.toString().replace('\\', '/');
        if (path.contains(packagePrefix)) {
          String filename = path.substring(path.indexOf(packagePrefix) + packagePrefix.length());
          return (!filename.isEmpty() && nameFilter.test(filename.substring(1)));
        } 
        return false;
      };
    return findFiles(inUrls, fileNamePredicate);
  }
  
  public static Iterable<File> findFiles(Collection<URL> urls, Predicate<File> filePredicate) {
    return () -> urls.stream().flatMap(()).filter(filePredicate).iterator();
  }
  
  public static java.io.File getFile(URL url) {
    try {
      String path = url.toURI().getSchemeSpecificPart();
      java.io.File file;
      if ((file = new java.io.File(path)).exists())
        return file; 
    } catch (URISyntaxException uRISyntaxException) {}
    try {
      String path = URLDecoder.decode(url.getPath(), "UTF-8");
      if (path.contains(".jar!"))
        path = path.substring(0, path.lastIndexOf(".jar!") + ".jar".length()); 
      java.io.File file;
      if ((file = new java.io.File(path)).exists())
        return file; 
    } catch (UnsupportedEncodingException unsupportedEncodingException) {}
    try {
      String path = url.toExternalForm();
      if (path.startsWith("jar:"))
        path = path.substring("jar:".length()); 
      if (path.startsWith("wsjar:"))
        path = path.substring("wsjar:".length()); 
      if (path.startsWith("file:"))
        path = path.substring("file:".length()); 
      if (path.contains(".jar!"))
        path = path.substring(0, path.indexOf(".jar!") + ".jar".length()); 
      if (path.contains(".war!"))
        path = path.substring(0, path.indexOf(".war!") + ".war".length()); 
      java.io.File file;
      if ((file = new java.io.File(path)).exists())
        return file; 
      path = path.replace("%20", " ");
      if ((file = new java.io.File(path)).exists())
        return file; 
    } catch (Exception exception) {}
    return null;
  }
  
  private static boolean hasJarFileInPath(URL url) {
    return url.toExternalForm().matches(".*\\.jar(!.*|$)");
  }
  
  private static boolean hasInnerJarFileInPath(URL url) {
    return url.toExternalForm().matches(".+\\.jar!/.+");
  }
  
  public enum DefaultUrlTypes implements UrlType {
    jarFile {
      public boolean matches(URL url) {
        return (url.getProtocol().equals("file") && Vfs.hasJarFileInPath(url));
      }
      
      public Vfs.Dir createDir(URL url) throws Exception {
        return new ZipDir(new JarFile(Vfs.getFile(url)));
      }
    },
    jarUrl {
      public boolean matches(URL url) {
        return (("jar".equals(url.getProtocol()) || "zip"
          .equals(url.getProtocol()) || "wsjar"
          .equals(url.getProtocol())) && 
          !Vfs.hasInnerJarFileInPath(url));
      }
      
      public Vfs.Dir createDir(URL url) throws Exception {
        try {
          URLConnection urlConnection = url.openConnection();
          if (urlConnection instanceof JarURLConnection) {
            urlConnection.setUseCaches(false);
            return new ZipDir(((JarURLConnection)urlConnection).getJarFile());
          } 
        } catch (Throwable throwable) {}
        java.io.File file = Vfs.getFile(url);
        if (file != null)
          return new ZipDir(new JarFile(file)); 
        return null;
      }
    },
    directory {
      public boolean matches(URL url) {
        if (url.getProtocol().equals("file") && !Vfs.hasJarFileInPath(url)) {
          java.io.File file = Vfs.getFile(url);
          return (file != null && file.isDirectory());
        } 
        return false;
      }
      
      public Vfs.Dir createDir(URL url) throws Exception {
        return new SystemDir(Vfs.getFile(url));
      }
    },
    jboss_vfs {
      public boolean matches(URL url) {
        return url.getProtocol().equals("vfs");
      }
      
      public Vfs.Dir createDir(URL url) throws Exception {
        return JbossDir.createDir(url);
      }
    },
    jboss_vfsfile {
      public boolean matches(URL url) throws Exception {
        return ("vfszip".equals(url.getProtocol()) || "vfsfile".equals(url.getProtocol()));
      }
      
      public Vfs.Dir createDir(URL url) throws Exception {
        return (new UrlTypeVFS()).createDir(url);
      }
    },
    bundle {
      public boolean matches(URL url) throws Exception {
        return url.getProtocol().startsWith("bundle");
      }
      
      public Vfs.Dir createDir(URL url) throws Exception {
        return Vfs.fromURL(
            
            (URL)ClasspathHelper.contextClassLoader()
            .loadClass("org.eclipse.core.runtime.FileLocator")
            .getMethod("resolve", new Class[] { URL.class }).invoke(null, new Object[] { url }));
      }
    },
    jarInputStream {
      public boolean matches(URL url) throws Exception {
        return url.toExternalForm().contains(".jar");
      }
      
      public Vfs.Dir createDir(URL url) throws Exception {
        return new JarInputDir(url);
      }
    };
  }
  
  public static interface Dir extends AutoCloseable {
    String getPath();
    
    Iterable<Vfs.File> getFiles();
    
    default void close() {}
  }
  
  public static interface File {
    String getName();
    
    String getRelativePath();
    
    InputStream openInputStream() throws IOException;
  }
  
  public static interface UrlType {
    boolean matches(URL param1URL) throws Exception;
    
    Vfs.Dir createDir(URL param1URL) throws Exception;
  }
}
