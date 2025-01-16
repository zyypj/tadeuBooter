package me.syncwrld.booter.libs.reflections.vfs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Iterator;
import me.syncwrld.booter.libs.reflections.ReflectionsException;

public class SystemDir implements Vfs.Dir {
  private final File file;
  
  public SystemDir(File file) {
    if (file != null && (!file.isDirectory() || !file.canRead()))
      throw new RuntimeException("cannot use dir " + file); 
    this.file = file;
  }
  
  public String getPath() {
    return (this.file != null) ? this.file.getPath().replace("\\", "/") : "/NO-SUCH-DIRECTORY/";
  }
  
  public Iterable<Vfs.File> getFiles() {
    if (this.file == null || !this.file.exists())
      return Collections.emptyList(); 
    return () -> {
        try {
          return Files.walk(this.file.toPath(), new java.nio.file.FileVisitOption[0]).filter(()).map(()).iterator();
        } catch (IOException e) {
          throw new ReflectionsException("could not get files for " + this.file, e);
        } 
      };
  }
}
