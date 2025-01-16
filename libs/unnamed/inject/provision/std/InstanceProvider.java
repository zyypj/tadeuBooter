package me.syncwrld.booter.libs.unnamed.inject.provision.std;

import me.syncwrld.booter.libs.unnamed.inject.Provider;
import me.syncwrld.booter.libs.unnamed.inject.key.Key;
import me.syncwrld.booter.libs.unnamed.inject.provision.StdProvider;
import me.syncwrld.booter.libs.unnamed.inject.scope.Scope;
import me.syncwrld.booter.libs.unnamed.inject.scope.Scopes;

public class InstanceProvider<T> extends StdProvider<T> implements Provider<T> {
  private final T instance;
  
  public InstanceProvider(T instance) {
    this.instance = instance;
    setInjected(true);
  }
  
  public Provider<T> withScope(Key<?> match, Scope scope) {
    if (scope == Scopes.SINGLETON)
      return this; 
    throw new UnsupportedOperationException("Instance providers cannot be scoped!");
  }
  
  public T get() {
    return this.instance;
  }
  
  public String toString() {
    return "instance '" + this.instance + "'";
  }
}
