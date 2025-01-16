package me.syncwrld.booter.libs.unnamed.inject.multibinding;

import java.util.Collection;
import me.syncwrld.booter.libs.unnamed.inject.Binder;
import me.syncwrld.booter.libs.unnamed.inject.Provider;
import me.syncwrld.booter.libs.unnamed.inject.impl.BinderImpl;
import me.syncwrld.booter.libs.unnamed.inject.impl.LinkedBuilder;
import me.syncwrld.booter.libs.unnamed.inject.key.Key;
import me.syncwrld.booter.libs.unnamed.inject.provision.Providers;
import me.syncwrld.booter.libs.unnamed.inject.provision.StdProvider;
import me.syncwrld.booter.libs.unnamed.inject.scope.Scope;
import me.syncwrld.booter.libs.unnamed.inject.util.Validate;

class CollectionMultiBindingBuilderImpl<E> implements Binder.CollectionMultiBindingBuilder<E>, LinkedBuilder<Binder.CollectionMultiBindingBuilder<E>, E> {
  private final BinderImpl binder;
  
  private final Key<? extends Collection<E>> collectionKey;
  
  private final Key<E> elementKey;
  
  private final CollectionCreator collectionCreator;
  
  public CollectionMultiBindingBuilderImpl(BinderImpl binder, Key<? extends Collection<E>> collectionKey, Key<E> elementKey, CollectionCreator collectionCreator) {
    this.binder = binder;
    this.collectionKey = collectionKey;
    this.elementKey = elementKey;
    this.collectionCreator = collectionCreator;
  }
  
  public void in(Scope scope) {
    Validate.notNull(scope, "scope", new Object[0]);
    Provider<? extends Collection<E>> provider = Providers.unwrap((Provider)this.binder.getProvider(this.collectionKey));
    if (provider != null) {
      if (provider instanceof StdProvider) {
        provider = ((StdProvider)provider).withScope(this.collectionKey, scope);
      } else {
        provider = scope.scope(provider);
      } 
      this.binder.$unsafeBind(this.collectionKey, provider);
    } 
  }
  
  public Key<E> key() {
    return this.elementKey;
  }
  
  public Binder.CollectionMultiBindingBuilder<E> toProvider(Provider<? extends E> provider) {
    Validate.notNull(provider, "provider", new Object[0]);
    StdProvider<? extends Collection<E>> collectionProvider = this.binder.getProvider(this.collectionKey);
    if (collectionProvider == null) {
      collectionProvider = new CollectionBoundProvider(this.collectionCreator);
      this.binder.$unsafeBind(this.collectionKey, (Provider)collectionProvider);
    } 
    Provider<? extends Collection<E>> delegate = Providers.unwrap((Provider)collectionProvider);
    if (!(delegate instanceof CollectionBoundProvider))
      throw new IllegalStateException("The key '" + this.collectionKey + "' is already bound and it isn't a multibinding!"); 
    CollectionBoundProvider<E> collectionDelegate = (CollectionBoundProvider)delegate;
    collectionDelegate.getModifiableProviderCollection().add(provider);
    return this;
  }
  
  public Binder.CollectionMultiBindingBuilder<E> toInstance(E instance) {
    return toProvider(Providers.instanceProvider(this.elementKey, instance));
  }
}
