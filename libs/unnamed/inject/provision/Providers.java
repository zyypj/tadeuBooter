package me.syncwrld.booter.libs.unnamed.inject.provision;

import me.syncwrld.booter.libs.unnamed.inject.Provider;
import me.syncwrld.booter.libs.unnamed.inject.impl.InjectorImpl;
import me.syncwrld.booter.libs.unnamed.inject.impl.ProvisionStack;
import me.syncwrld.booter.libs.unnamed.inject.key.Key;
import me.syncwrld.booter.libs.unnamed.inject.key.TypeReference;
import me.syncwrld.booter.libs.unnamed.inject.provision.std.InstanceProvider;
import me.syncwrld.booter.libs.unnamed.inject.provision.std.LinkedProvider;
import me.syncwrld.booter.libs.unnamed.inject.provision.std.ProviderTypeProvider;
import me.syncwrld.booter.libs.unnamed.inject.provision.std.ScopedProvider;
import me.syncwrld.booter.libs.unnamed.inject.util.Validate;

public final class Providers {
  public static void inject(ProvisionStack stack, InjectorImpl injector, Provider<?> provider) {
    if (provider instanceof StdProvider) {
      ((StdProvider)provider).inject(stack, injector);
    } else {
      injector.injectMembers(stack, Key.of(TypeReference.of(provider.getClass())), provider);
    } 
  }
  
  public static <T> Provider<T> unwrap(Provider<T> provider) {
    if (provider instanceof DelegatingStdProvider)
      return unwrap(((DelegatingStdProvider<T>)provider).getDelegate()); 
    if (provider instanceof ScopedProvider)
      return unwrap(((ScopedProvider)provider).getUnscoped()); 
    return provider;
  }
  
  public static <T> StdProvider<T> normalize(Provider<T> provider) {
    if (provider instanceof StdProvider)
      return (StdProvider<T>)provider; 
    return new DelegatingStdProvider<>(provider);
  }
  
  public static <T> Provider<? extends T> instanceProvider(Key<T> key, T instance) {
    Validate.notNull(key, "key", new Object[0]);
    Validate.notNull(instance, "instance", new Object[0]);
    return (Provider<? extends T>)new InstanceProvider(instance);
  }
  
  public static <T> Provider<? extends T> providerTypeProvider(TypeReference<? extends Provider<? extends T>> providerClass) {
    Validate.notNull(providerClass);
    return (Provider<? extends T>)new ProviderTypeProvider(providerClass);
  }
  
  public static <T> Provider<? extends T> link(Key<T> key, Key<? extends T> target) {
    Validate.notNull(key, "key", new Object[0]);
    Validate.notNull(target, "target", new Object[0]);
    return (Provider<? extends T>)new LinkedProvider(key, target);
  }
}
