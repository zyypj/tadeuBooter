package me.syncwrld.booter.libs.reflections.vfs;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;
import me.syncwrld.booter.libs.reflections.Reflections;
import me.syncwrld.booter.libs.reflections.ReflectionsException;

public class JarInputDir implements Vfs.Dir {
  private final URL url;
  
  JarInputStream jarInputStream;
  
  long cursor = 0L;
  
  long nextCursor = 0L;
  
  public JarInputDir(URL url) {
    this.url = url;
  }
  
  public String getPath() {
    return this.url.getPath();
  }
  
  public Iterable<Vfs.File> getFiles() {
    return () -> new Iterator<Vfs.File>() {
        Vfs.File entry;
        
        public boolean hasNext() {
          return (this.entry != null || (this.entry = computeNext()) != null);
        }
        
        public Vfs.File next() {
          Vfs.File next = this.entry;
          this.entry = null;
          return next;
        }
        
        private Vfs.File computeNext() {
          try {
            while (true) {
              ZipEntry entry = JarInputDir.this.jarInputStream.getNextJarEntry();
              if (entry == null)
                return null; 
              long size = entry.getSize();
              if (size < 0L)
                size = 4294967295L + size; 
              JarInputDir.this.nextCursor += size;
              if (!entry.isDirectory())
                return new JarInputFile(entry, JarInputDir.this, JarInputDir.this.cursor, JarInputDir.this.nextCursor); 
            } 
          } catch (IOException e) {
            throw new ReflectionsException("could not get next zip entry", e);
          } 
        }
      };
  }
  
  public void close() {
    try {
      if (this.jarInputStream != null)
        this.jarInputStream.close(); 
    } catch (IOException e) {
      if (Reflections.log != null)
        Reflections.log.warning("Could not close InputStream, exception: " + e); 
    } 
  }
}
