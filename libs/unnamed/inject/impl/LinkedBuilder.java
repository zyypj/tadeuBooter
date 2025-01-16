package me.syncwrld.booter.libs.unnamed.inject.impl;

import me.syncwrld.booter.libs.unnamed.inject.Binder;
import me.syncwrld.booter.libs.unnamed.inject.Provider;
import me.syncwrld.booter.libs.unnamed.inject.assisted.ValueFactory;
import me.syncwrld.booter.libs.unnamed.inject.assisted.provision.ToFactoryProvider;
import me.syncwrld.booter.libs.unnamed.inject.key.Key;
import me.syncwrld.booter.libs.unnamed.inject.key.TypeReference;
import me.syncwrld.booter.libs.unnamed.inject.provision.Providers;
import me.syncwrld.booter.libs.unnamed.inject.provision.std.generic.GenericProvider;
import me.syncwrld.booter.libs.unnamed.inject.provision.std.generic.ToGenericProvider;
import me.syncwrld.booter.libs.unnamed.inject.util.Validate;

public interface LinkedBuilder<R, T> extends Binder.Linked<R, T> {
  Key<T> key();
  
  default R toGenericProvider(GenericProvider<? extends T> provider) {
    Validate.notNull(provider, "provider", new Object[0]);
    return (R)toProvider((Provider)new ToGenericProvider(provider));
  }
  
  default void toFactory(TypeReference<? extends ValueFactory> factory) {
    Validate.notNull(factory, "factory", new Object[0]);
    toProvider((Provider)new ToFactoryProvider(factory));
  }
  
  default R to(TypeReference<? extends T> targetType) {
    Validate.notNull(targetType, "targetType", new Object[0]);
    return (R)toProvider(Providers.link(key(), Key.of(targetType)));
  }
  
  default <P extends Provider<? extends T>> R toProvider(TypeReference<P> providerClass) {
    Validate.notNull(providerClass, "providerClass", new Object[0]);
    return (R)toProvider(Providers.providerTypeProvider(providerClass));
  }
}
