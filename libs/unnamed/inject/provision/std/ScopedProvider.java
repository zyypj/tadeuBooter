package me.syncwrld.booter.libs.unnamed.inject.provision.std;

import me.syncwrld.booter.libs.unnamed.inject.Provider;
import me.syncwrld.booter.libs.unnamed.inject.impl.InjectorImpl;
import me.syncwrld.booter.libs.unnamed.inject.impl.ProvisionStack;
import me.syncwrld.booter.libs.unnamed.inject.key.Key;
import me.syncwrld.booter.libs.unnamed.inject.provision.Providers;
import me.syncwrld.booter.libs.unnamed.inject.provision.StdProvider;
import me.syncwrld.booter.libs.unnamed.inject.scope.Scope;
import me.syncwrld.booter.libs.unnamed.inject.util.Validate;

public class ScopedProvider<T> extends StdProvider<T> implements Provider<T> {
  private final Provider<T> unscoped;
  
  private final Provider<T> scoped;
  
  private final Scope scope;
  
  public ScopedProvider(Provider<T> provider, Scope scope) {
    this.unscoped = (Provider<T>)Validate.notNull(provider, "provider", new Object[0]);
    this.scope = (Scope)Validate.notNull(scope, "scope", new Object[0]);
    this.scoped = scope.scope(provider);
  }
  
  protected ScopedProvider() {
    this.unscoped = null;
    this.scoped = null;
    this.scope = null;
  }
  
  public Provider<T> withScope(Key<?> match, Scope scope) {
    if (this.scope == scope)
      return this; 
    throw new UnsupportedOperationException("Cannot scope the provider again! Scope: " + scope
        .getClass().getSimpleName() + ". Provider: " + this.unscoped);
  }
  
  public void inject(ProvisionStack stack, InjectorImpl injector) {
    Providers.inject(stack, injector, this.unscoped);
    Providers.inject(stack, injector, this.scoped);
    this.injected = true;
  }
  
  public T get() {
    return (T)this.scoped.get();
  }
  
  public Provider<T> getUnscoped() {
    return this.unscoped;
  }
  
  public Provider<T> getScoped() {
    return this.scoped;
  }
  
  public Scope getScope() {
    return this.scope;
  }
  
  public boolean requiresJitScoping() {
    return false;
  }
}
