package me.syncwrld.booter.libs.google.gson.internal.bind;

import me.syncwrld.booter.libs.google.gson.TypeAdapter;

public abstract class SerializationDelegatingTypeAdapter<T> extends TypeAdapter<T> {
  public abstract TypeAdapter<T> getSerializationDelegate();
}
