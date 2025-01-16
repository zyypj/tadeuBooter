package me.syncwrld.booter.libs.google.gson.internal;

import java.io.IOException;
import me.syncwrld.booter.libs.google.gson.stream.JsonReader;

public abstract class JsonReaderInternalAccess {
  public static JsonReaderInternalAccess INSTANCE;
  
  public abstract void promoteNameToValue(JsonReader paramJsonReader) throws IOException;
}
