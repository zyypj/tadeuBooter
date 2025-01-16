package me.syncwrld.booter.libs.reflections.serializers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import me.syncwrld.booter.libs.google.gson.GsonBuilder;
import me.syncwrld.booter.libs.reflections.Reflections;

public class JsonSerializer implements Serializer {
  public Reflections read(InputStream inputStream) {
    return (Reflections)(new GsonBuilder()).setPrettyPrinting().create()
      .fromJson(new InputStreamReader(inputStream), Reflections.class);
  }
  
  public File save(Reflections reflections, String filename) {
    try {
      File file = Serializer.prepareFile(filename);
      String json = (new GsonBuilder()).setPrettyPrinting().create().toJson(reflections);
      Files.write(file.toPath(), json.getBytes(Charset.defaultCharset()), new java.nio.file.OpenOption[0]);
      return file;
    } catch (IOException e) {
      throw new RuntimeException(e);
    } 
  }
}
