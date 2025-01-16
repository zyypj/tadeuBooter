package me.syncwrld.booter.libs.unnamed.inject.multibinding;

import java.util.Collection;
import java.util.Collections;
import me.syncwrld.booter.libs.unnamed.inject.Provider;
import me.syncwrld.booter.libs.unnamed.inject.impl.InjectorImpl;
import me.syncwrld.booter.libs.unnamed.inject.impl.ProvisionStack;
import me.syncwrld.booter.libs.unnamed.inject.key.Key;
import me.syncwrld.booter.libs.unnamed.inject.key.TypeReference;
import me.syncwrld.booter.libs.unnamed.inject.provision.StdProvider;

class CollectionBoundProvider<E> extends StdProvider<Collection<E>> {
  private final Collection<Provider<? extends E>> delegates;
  
  private final CollectionCreator collectionCreator;
  
  CollectionBoundProvider(CollectionCreator collectionCreator) {
    this.collectionCreator = collectionCreator;
    this.delegates = collectionCreator.create();
  }
  
  public void inject(ProvisionStack stack, InjectorImpl injector) {
    for (Provider<? extends E> provider : this.delegates) {
      if (provider instanceof StdProvider) {
        ((StdProvider)provider).inject(stack, injector);
        continue;
      } 
      injector.injectMembers(stack, 
          
          Key.of(TypeReference.of(provider.getClass())), provider);
    } 
    this.injected = true;
  }
  
  public Collection<E> get() {
    Collection<E> collection = this.collectionCreator.create();
    for (Provider<? extends E> delegate : this.delegates)
      collection.add((E)delegate.get()); 
    return collection;
  }
  
  public Collection<Provider<? extends E>> getProviders() {
    return Collections.unmodifiableCollection(this.delegates);
  }
  
  Collection<Provider<? extends E>> getModifiableProviderCollection() {
    return this.delegates;
  }
  
  public String toString() {
    return "CollectionMultiBound(" + this.delegates + ")";
  }
}
