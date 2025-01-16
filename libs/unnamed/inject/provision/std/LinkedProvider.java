package me.syncwrld.booter.libs.unnamed.inject.provision.std;

import me.syncwrld.booter.libs.unnamed.inject.impl.InjectorImpl;
import me.syncwrld.booter.libs.unnamed.inject.impl.ProvisionStack;
import me.syncwrld.booter.libs.unnamed.inject.key.Key;
import me.syncwrld.booter.libs.unnamed.inject.provision.StdProvider;

public class LinkedProvider<T> extends StdProvider<T> {
  private final Key<T> key;
  
  private final Key<? extends T> target;
  
  private final boolean autoBound;
  
  private InjectorImpl injector;
  
  public LinkedProvider(Key<T> key, Key<? extends T> target) {
    this.key = key;
    this.target = target;
    this.autoBound = key.equals(target);
  }
  
  public void inject(ProvisionStack stack, InjectorImpl injector) {
    this.injector = injector;
    this.injected = true;
  }
  
  public T get() {
    return (T)this.injector.getInstance(this.target, !this.autoBound);
  }
  
  public boolean isAutoBound() {
    return this.autoBound;
  }
  
  public Key<? extends T> getTarget() {
    return this.target;
  }
  
  public String toString() {
    if (this.key.equals(this.target))
      return "same key"; 
    return "linked key '" + this.target + "'";
  }
}
