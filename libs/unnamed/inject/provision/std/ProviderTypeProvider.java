package me.syncwrld.booter.libs.unnamed.inject.provision.std;

import me.syncwrld.booter.libs.unnamed.inject.Provider;
import me.syncwrld.booter.libs.unnamed.inject.impl.InjectorImpl;
import me.syncwrld.booter.libs.unnamed.inject.impl.ProvisionStack;
import me.syncwrld.booter.libs.unnamed.inject.key.TypeReference;
import me.syncwrld.booter.libs.unnamed.inject.provision.StdProvider;

public class ProviderTypeProvider<T> extends StdProvider<T> implements Provider<T> {
  private final TypeReference<? extends Provider<? extends T>> providerClass;
  
  private volatile Provider<? extends T> provider;
  
  public ProviderTypeProvider(TypeReference<? extends Provider<? extends T>> providerClass) {
    this.providerClass = providerClass;
  }
  
  public void inject(ProvisionStack stack, InjectorImpl injector) {
    this.provider = (Provider<? extends T>)injector.getInstance(this.providerClass);
    this.injected = true;
  }
  
  public T get() {
    return (T)this.provider.get();
  }
  
  public Provider<? extends T> getProvider() {
    return this.provider;
  }
  
  public String toString() {
    return "ClassProvider(" + this.providerClass + ")";
  }
}
