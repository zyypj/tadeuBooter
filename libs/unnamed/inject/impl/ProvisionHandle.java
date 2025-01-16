package me.syncwrld.booter.libs.unnamed.inject.impl;

import me.syncwrld.booter.libs.unnamed.inject.Provider;
import me.syncwrld.booter.libs.unnamed.inject.key.Key;
import me.syncwrld.booter.libs.unnamed.inject.provision.Providers;
import me.syncwrld.booter.libs.unnamed.inject.provision.StdProvider;
import me.syncwrld.booter.libs.unnamed.inject.provision.std.ScopedProvider;

public class ProvisionHandle {
  private final InjectorImpl injector;
  
  private final BinderImpl binder;
  
  public ProvisionHandle(InjectorImpl injector, BinderImpl binder) {
    this.injector = injector;
    this.binder = binder;
  }
  
  private <T> StdProvider<T> getGenericProvider(Class<T> rawType, Key<T> match) {
    Key<T> rawTypeKey = Key.of(rawType);
    StdProvider<T> provider = this.binder.getProvider(rawTypeKey);
    if (provider instanceof ScopedProvider) {
      ScopedProvider<T> scopedProvider = (ScopedProvider<T>)provider;
      if (scopedProvider.requiresJitScoping()) {
        provider = (StdProvider<T>)scopedProvider.withScope(match, scopedProvider.getScope());
        this.binder.$unsafeBind(match, (Provider<?>)provider);
      } 
    } 
    if (!(provider instanceof me.syncwrld.booter.libs.unnamed.inject.provision.std.generic.ToGenericProvider.SyntheticGenericProvider))
      return null; 
    return provider;
  }
  
  public <T> StdProvider<T> getProviderAndInject(ProvisionStack stack, Key<T> key) {
    StdProvider<T> provider = this.binder.getProvider(key);
    if (provider == null) {
      Class<T> rawType = key.getType().getRawType();
      if (key.getType().getType() != rawType) {
        if ((provider = getGenericProvider(rawType, key)) == null)
          return null; 
      } else {
        return null;
      } 
    } 
    if (!provider.isInjected())
      Providers.inject(stack, this.injector, (Provider)provider); 
    return provider;
  }
}
