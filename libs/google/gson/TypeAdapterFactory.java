package me.syncwrld.booter.libs.google.gson;

import me.syncwrld.booter.libs.google.gson.reflect.TypeToken;

public interface TypeAdapterFactory {
  <T> TypeAdapter<T> create(Gson paramGson, TypeToken<T> paramTypeToken);
}
