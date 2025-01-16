package me.syncwrld.booter.libs.unnamed.inject.provision.std.generic;

import me.syncwrld.booter.libs.unnamed.inject.Provider;
import me.syncwrld.booter.libs.unnamed.inject.key.Key;

public interface GenericProvider<T> {
  T get(Key<?> paramKey);
  
  default Provider<T> asConstantProvider(Key<?> match) {
    return () -> get(match);
  }
}
