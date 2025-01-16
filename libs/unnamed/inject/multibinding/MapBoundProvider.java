package me.syncwrld.booter.libs.unnamed.inject.multibinding;

import java.util.Collections;
import java.util.Map;
import me.syncwrld.booter.libs.unnamed.inject.Provider;
import me.syncwrld.booter.libs.unnamed.inject.impl.InjectorImpl;
import me.syncwrld.booter.libs.unnamed.inject.impl.ProvisionStack;
import me.syncwrld.booter.libs.unnamed.inject.provision.Providers;
import me.syncwrld.booter.libs.unnamed.inject.provision.StdProvider;

class MapBoundProvider<K, V> extends StdProvider<Map<K, V>> {
  private final Map<K, Provider<? extends V>> delegates;
  
  private final MapCreator mapCreator;
  
  MapBoundProvider(MapCreator mapCreator) {
    this.delegates = mapCreator.create();
    this.mapCreator = mapCreator;
  }
  
  public void inject(ProvisionStack stack, InjectorImpl injector) {
    this.delegates.forEach((key, valueProvider) -> Providers.inject(stack, injector, valueProvider));
    this.injected = true;
  }
  
  public Map<K, V> get() {
    Map<K, V> map = this.mapCreator.create();
    this.delegates.forEach((key, valueProvider) -> map.put(key, valueProvider.get()));
    return map;
  }
  
  public Map<K, Provider<? extends V>> getProviders() {
    return Collections.unmodifiableMap(this.delegates);
  }
  
  Map<K, Provider<? extends V>> getModifiableProviderMap() {
    return this.delegates;
  }
  
  public String toString() {
    return "MapMultiBound(" + this.delegates + ")";
  }
}
