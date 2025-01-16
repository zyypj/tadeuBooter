package me.syncwrld.booter.libs.unnamed.inject.provision;

import me.syncwrld.booter.libs.unnamed.inject.Provider;
import me.syncwrld.booter.libs.unnamed.inject.impl.BinderImpl;
import me.syncwrld.booter.libs.unnamed.inject.impl.InjectorImpl;
import me.syncwrld.booter.libs.unnamed.inject.impl.ProvisionStack;
import me.syncwrld.booter.libs.unnamed.inject.key.Key;
import me.syncwrld.booter.libs.unnamed.inject.provision.std.ScopedProvider;
import me.syncwrld.booter.libs.unnamed.inject.scope.Scope;

public abstract class StdProvider<T> implements Provider<T> {
  protected boolean injected;
  
  public boolean isInjected() {
    return this.injected;
  }
  
  public void setInjected(boolean injected) {
    this.injected = injected;
  }
  
  public Provider<T> withScope(Key<?> match, Scope scope) {
    ScopedProvider scopedProvider = new ScopedProvider(this, scope);
    ((StdProvider)scopedProvider).injected = this.injected;
    return (Provider<T>)scopedProvider;
  }
  
  public void inject(ProvisionStack stack, InjectorImpl injector) {
    this.injected = true;
    injector.injectMembers(this);
  }
  
  public boolean onBind(BinderImpl binder, Key<?> key) {
    return true;
  }
  
  public T get() {
    return null;
  }
  
  public T get(Key<?> match) {
    return get();
  }
}
