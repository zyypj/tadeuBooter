package me.syncwrld.booter.libs.unnamed.inject.multibinding;

import java.util.Map;
import me.syncwrld.booter.libs.unnamed.inject.Binder;
import me.syncwrld.booter.libs.unnamed.inject.Provider;
import me.syncwrld.booter.libs.unnamed.inject.impl.BinderImpl;
import me.syncwrld.booter.libs.unnamed.inject.impl.LinkedBuilder;
import me.syncwrld.booter.libs.unnamed.inject.key.Key;
import me.syncwrld.booter.libs.unnamed.inject.provision.Providers;
import me.syncwrld.booter.libs.unnamed.inject.provision.StdProvider;
import me.syncwrld.booter.libs.unnamed.inject.scope.Scope;
import me.syncwrld.booter.libs.unnamed.inject.util.Validate;

class MapMultiBindingBuilderImpl<K, V> implements Binder.MapMultiBindingBuilder<K, V> {
  private final BinderImpl binder;
  
  private final MapCreator mapCreator;
  
  private final Key<Map<K, V>> mapKey;
  
  private final Key<V> valueKey;
  
  MapMultiBindingBuilderImpl(BinderImpl binder, MapCreator mapCreator, Key<Map<K, V>> mapKey, Key<V> valueKey) {
    this.binder = binder;
    this.mapCreator = mapCreator;
    this.mapKey = mapKey;
    this.valueKey = valueKey;
  }
  
  public void in(Scope scope) {
    Validate.notNull(scope, "scope", new Object[0]);
    Provider<? extends Map<K, V>> provider = Providers.unwrap((Provider)this.binder.getProvider(this.mapKey));
    if (provider != null) {
      if (provider instanceof StdProvider) {
        provider = ((StdProvider)provider).withScope(this.mapKey, scope);
      } else {
        provider = scope.scope(provider);
      } 
      this.binder.$unsafeBind(this.mapKey, provider);
    } 
  }
  
  public Binder.KeyBinder<K, V> bind(K key) {
    return new KeyBinderImpl(key);
  }
  
  class KeyBinderImpl implements Binder.KeyBinder<K, V>, LinkedBuilder<Binder.MapMultiBindingBuilder<K, V>, V> {
    private final K key;
    
    private KeyBinderImpl(K key) {
      this.key = key;
    }
    
    public Key<V> key() {
      return MapMultiBindingBuilderImpl.this.valueKey;
    }
    
    public Binder.MapMultiBindingBuilder<K, V> toProvider(Provider<? extends V> provider) {
      Validate.notNull(provider, "provider", new Object[0]);
      StdProvider<? extends Map<K, V>> mapProvider = MapMultiBindingBuilderImpl.this.binder.getProvider(MapMultiBindingBuilderImpl.this.mapKey);
      if (mapProvider == null) {
        mapProvider = (StdProvider)new MapBoundProvider<>(MapMultiBindingBuilderImpl.this.mapCreator);
        MapMultiBindingBuilderImpl.this.binder.$unsafeBind(MapMultiBindingBuilderImpl.this.mapKey, (Provider)mapProvider);
      } 
      Provider<? extends Map<K, V>> delegate = Providers.unwrap((Provider)mapProvider);
      if (!(delegate instanceof MapBoundProvider))
        throw new IllegalStateException("The key '" + MapMultiBindingBuilderImpl.this.mapKey + "' is already bound and it isn't a multibinding!"); 
      MapBoundProvider<K, V> collectionDelegate = (MapBoundProvider)delegate;
      collectionDelegate.getModifiableProviderMap().put(this.key, provider);
      return MapMultiBindingBuilderImpl.this;
    }
    
    public Binder.MapMultiBindingBuilder<K, V> toInstance(V instance) {
      return toProvider(Providers.instanceProvider(key(), instance));
    }
  }
}
