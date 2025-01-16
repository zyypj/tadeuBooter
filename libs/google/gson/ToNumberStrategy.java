package me.syncwrld.booter.libs.google.gson;

import java.io.IOException;
import me.syncwrld.booter.libs.google.gson.stream.JsonReader;

public interface ToNumberStrategy {
  Number readNumber(JsonReader paramJsonReader) throws IOException;
}
