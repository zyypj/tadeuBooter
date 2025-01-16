package me.syncwrld.booter.libs.unnamed.inject;

import java.util.Arrays;
import me.syncwrld.booter.libs.unnamed.inject.impl.BinderImpl;
import me.syncwrld.booter.libs.unnamed.inject.impl.InjectorImpl;
import me.syncwrld.booter.libs.unnamed.inject.key.TypeReference;

public interface Injector {
  static Injector create(Module... modules) {
    return create(Arrays.asList(modules));
  }
  
  static Injector create(Iterable<? extends Module> modules) {
    BinderImpl binder = new BinderImpl();
    binder.install(modules);
    if (binder.hasErrors())
      binder.reportAttachedErrors(); 
    return (Injector)new InjectorImpl(binder);
  }
  
  default <T> Provider<? extends T> getProvider(Class<T> key) {
    return getProvider(TypeReference.of(key));
  }
  
  <T> Provider<? extends T> getProvider(TypeReference<T> paramTypeReference);
  
  void injectStaticMembers(Class<?> paramClass);
  
  default void injectMembers(Object object) {
    injectMembers(TypeReference.of(object.getClass()), object);
  }
  
  <T> void injectMembers(TypeReference<T> paramTypeReference, T paramT);
  
  default <T> T getInstance(Class<T> type) {
    return getInstance(TypeReference.of(type));
  }
  
  <T> T getInstance(TypeReference<T> paramTypeReference);
}
