package me.syncwrld.booter.libs.unnamed.inject.impl;

import me.syncwrld.booter.libs.unnamed.inject.Binder;
import me.syncwrld.booter.libs.unnamed.inject.Provider;
import me.syncwrld.booter.libs.unnamed.inject.key.Key;
import me.syncwrld.booter.libs.unnamed.inject.key.TypeReference;
import me.syncwrld.booter.libs.unnamed.inject.provision.Providers;
import me.syncwrld.booter.libs.unnamed.inject.scope.Scope;
import me.syncwrld.booter.libs.unnamed.inject.util.Validate;

class BindingBuilderImpl<T> implements Binder.QualifiedBindingBuilder<T>, KeyBuilder<Binder.QualifiedBindingBuilder<T>, T>, LinkedBuilder<Binder.Scoped, T> {
  private final BinderImpl binder;
  
  private Key<T> key;
  
  protected BindingBuilderImpl(BinderImpl binder, TypeReference<T> key) {
    this.key = Key.of(key);
    this.binder = binder;
  }
  
  public void in(Scope scope) {
    Validate.notNull(scope, "scope", new Object[0]);
    selfBindingIfNotBound();
    this.binder.$unsafeBind(this.key, this.binder
        
        .<T>getProvider(this.key)
        .withScope(this.key, scope));
  }
  
  public Binder.Scoped toProvider(Provider<? extends T> provider) {
    Validate.notNull(provider, "provider", new Object[0]);
    requireNotBound();
    this.binder.$unsafeBind(this.key, provider);
    return (Binder.Scoped)this;
  }
  
  public void toInstance(T instance) {
    Validate.notNull(instance, "instance", new Object[0]);
    toProvider(Providers.instanceProvider(this.key, instance));
  }
  
  private void requireNotBound() {
    if (this.binder.getProvider(this.key) != null)
      throw new IllegalStateException("The key is already bound"); 
  }
  
  private void selfBindingIfNotBound() {
    if (this.binder.getProvider(this.key) == null)
      toProvider(Providers.link(this.key, this.key)); 
  }
  
  public Key<T> key() {
    return this.key;
  }
  
  public void setKey(Key<T> key) {
    this.key = key;
  }
  
  public Binder.QualifiedBindingBuilder<T> getReturnValue() {
    return this;
  }
}
