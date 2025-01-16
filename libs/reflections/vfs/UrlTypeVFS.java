package me.syncwrld.booter.libs.reflections.vfs;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Predicate;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.syncwrld.booter.libs.reflections.Reflections;
import me.syncwrld.booter.libs.reflections.ReflectionsException;

public class UrlTypeVFS implements Vfs.UrlType {
  public static final String[] REPLACE_EXTENSION = new String[] { ".ear/", ".jar/", ".war/", ".sar/", ".har/", ".par/" };
  
  final String VFSZIP = "vfszip";
  
  final String VFSFILE = "vfsfile";
  
  public boolean matches(URL url) {
    return ("vfszip".equals(url.getProtocol()) || "vfsfile".equals(url.getProtocol()));
  }
  
  public Vfs.Dir createDir(URL url) {
    try {
      URL adaptedUrl = adaptURL(url);
      return new ZipDir(new JarFile(adaptedUrl.getFile()));
    } catch (Exception e) {
      try {
        return new ZipDir(new JarFile(url.getFile()));
      } catch (IOException e1) {
        if (Reflections.log != null)
          Reflections.log.warning("Could not get URL, exception message: " + e); 
        return null;
      } 
    } 
  }
  
  public URL adaptURL(URL url) throws MalformedURLException {
    if ("vfszip".equals(url.getProtocol()))
      return replaceZipSeparators(url.getPath(), file -> (file.exists() && file.isFile())); 
    if ("vfsfile".equals(url.getProtocol()))
      return new URL(url.toString().replace("vfsfile", "file")); 
    return url;
  }
  
  URL replaceZipSeparators(String path, Predicate<File> acceptFile) throws MalformedURLException {
    int pos = 0;
    while (pos != -1) {
      pos = findFirstMatchOfDeployableExtention(path, pos);
      if (pos > 0) {
        File file = new File(path.substring(0, pos - 1));
        if (acceptFile.test(file))
          return replaceZipSeparatorStartingFrom(path, pos); 
      } 
    } 
    throw new ReflectionsException("Unable to identify the real zip file in path '" + path + "'.");
  }
  
  int findFirstMatchOfDeployableExtention(String path, int pos) {
    Pattern p = Pattern.compile("\\.[ejprw]ar/");
    Matcher m = p.matcher(path);
    if (m.find(pos))
      return m.end(); 
    return -1;
  }
  
  URL replaceZipSeparatorStartingFrom(String path, int pos) throws MalformedURLException {
    String zipFile = path.substring(0, pos - 1);
    String zipPath = path.substring(pos);
    int numSubs = 1;
    for (String ext : REPLACE_EXTENSION) {
      while (zipPath.contains(ext)) {
        zipPath = zipPath.replace(ext, ext.substring(0, 4) + "!");
        numSubs++;
      } 
    } 
    String prefix = "";
    for (int i = 0; i < numSubs; i++)
      prefix = prefix + "zip:"; 
    if (zipPath.trim().length() == 0)
      return new URL(prefix + "/" + zipFile); 
    return new URL(prefix + "/" + zipFile + "!" + zipPath);
  }
}
