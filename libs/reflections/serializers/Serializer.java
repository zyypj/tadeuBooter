package me.syncwrld.booter.libs.reflections.serializers;

import java.io.File;
import java.io.InputStream;
import me.syncwrld.booter.libs.reflections.Reflections;

public interface Serializer {
  Reflections read(InputStream paramInputStream);
  
  File save(Reflections paramReflections, String paramString);
  
  static File prepareFile(String filename) {
    File file = new File(filename);
    File parent = file.getAbsoluteFile().getParentFile();
    if (!parent.exists())
      parent.mkdirs(); 
    return file;
  }
}
