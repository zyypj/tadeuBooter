package me.syncwrld.booter.libs.unnamed.inject.impl;

import java.lang.reflect.Modifier;
import me.syncwrld.booter.libs.unnamed.inject.ProvidedBy;
import me.syncwrld.booter.libs.unnamed.inject.Provider;
import me.syncwrld.booter.libs.unnamed.inject.Targetted;
import me.syncwrld.booter.libs.unnamed.inject.key.Key;
import me.syncwrld.booter.libs.unnamed.inject.key.TypeReference;
import me.syncwrld.booter.libs.unnamed.inject.provision.Providers;
import me.syncwrld.booter.libs.unnamed.inject.provision.StdProvider;
import me.syncwrld.booter.libs.unnamed.inject.scope.Scope;
import me.syncwrld.booter.libs.unnamed.inject.scope.Scopes;

final class AnnotationScanner {
  static <T> void bind(TypeReference<T> keyType, BinderImpl binder) {
    Key<T> key = Key.of(keyType);
    StdProvider<? extends T> provider = binder.getProvider(key);
    if (provider != null)
      return; 
    Class<? super T> rawType = keyType.getRawType();
    Targetted target = rawType.<Targetted>getAnnotation(Targetted.class);
    ProvidedBy providedBy = rawType.<ProvidedBy>getAnnotation(ProvidedBy.class);
    if (target != null) {
      Key<? extends T> linkedKey = Key.of(TypeReference.of(target.value()));
      binder.$unsafeBind(key, Providers.link(key, linkedKey));
    } else if (providedBy != null) {
      TypeReference<? extends Provider<? extends T>> linkedProvider = TypeReference.of(providedBy.value());
      binder.$unsafeBind(key, Providers.providerTypeProvider(linkedProvider));
    } 
  }
  
  static <T> void scope(TypeReference<T> keyType, BinderImpl binder) {
    Key<T> key = Key.of(keyType);
    StdProvider<? extends T> provider = binder.getProvider(key);
    Class<? super T> rawType = keyType.getRawType();
    if (provider == null && !rawType.isInterface() && 
      !Modifier.isAbstract(rawType.getModifiers()))
      provider = Providers.normalize(Providers.link(key, key)); 
    if (provider == null)
      return; 
    Scope scope = Scopes.getScanner().scan(rawType);
    if (scope != Scopes.NONE)
      binder.$unsafeBind(key, provider.withScope(key, scope)); 
  }
}
