package me.syncwrld.booter.libs.reflections.vfs;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.Iterator;
import java.util.Stack;
import java.util.jar.JarFile;
import org.jboss.vfs.VirtualFile;

public class JbossDir implements Vfs.Dir {
  private final VirtualFile virtualFile;
  
  private JbossDir(VirtualFile virtualFile) {
    this.virtualFile = virtualFile;
  }
  
  public static Vfs.Dir createDir(URL url) throws Exception {
    Object content = url.openConnection().getContent();
    if (content instanceof org.jboss.vfs.VirtualJarInputStream) {
      Field root = content.getClass().getDeclaredField("root");
      root.setAccessible(true);
      content = root.get(content);
    } 
    VirtualFile virtualFile = (VirtualFile)content;
    if (virtualFile.isFile())
      return new ZipDir(new JarFile(virtualFile.getPhysicalFile())); 
    return new JbossDir(virtualFile);
  }
  
  public String getPath() {
    return this.virtualFile.getPathName();
  }
  
  public Iterable<Vfs.File> getFiles() {
    return () -> new Iterator<Vfs.File>() {
        Vfs.File entry;
        
        final Stack stack;
        
        public boolean hasNext() {
          return (this.entry != null || (this.entry = computeNext()) != null);
        }
        
        public Vfs.File next() {
          Vfs.File next = this.entry;
          this.entry = null;
          return next;
        }
        
        private Vfs.File computeNext() {
          while (!this.stack.isEmpty()) {
            VirtualFile file = this.stack.pop();
            if (file.isDirectory()) {
              this.stack.addAll(file.getChildren());
              continue;
            } 
            return new JbossFile(JbossDir.this, file);
          } 
          return null;
        }
      };
  }
}
