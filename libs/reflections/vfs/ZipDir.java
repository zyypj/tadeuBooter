package me.syncwrld.booter.libs.reflections.vfs;

import java.io.IOException;
import java.util.Iterator;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import me.syncwrld.booter.libs.reflections.Reflections;

public class ZipDir implements Vfs.Dir {
  final ZipFile jarFile;
  
  public ZipDir(JarFile jarFile) {
    this.jarFile = jarFile;
  }
  
  public String getPath() {
    return (this.jarFile != null) ? this.jarFile.getName().replace("\\", "/") : "/NO-SUCH-DIRECTORY/";
  }
  
  public Iterable<Vfs.File> getFiles() {
    return () -> this.jarFile.stream().filter(()).map(()).iterator();
  }
  
  public void close() {
    try {
      this.jarFile.close();
    } catch (IOException e) {
      if (Reflections.log != null)
        Reflections.log.warning("Could not close JarFile, exception: " + e); 
    } 
  }
  
  public String toString() {
    return this.jarFile.getName();
  }
}
