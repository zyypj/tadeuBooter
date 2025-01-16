package me.syncwrld.booter.libs.unnamed.inject.scope;

import me.syncwrld.booter.libs.unnamed.inject.Provider;

public interface Scope {
  <T> Provider<T> scope(Provider<T> paramProvider);
}
