package me.syncwrld.booter.libs.unnamed.inject;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import me.syncwrld.booter.libs.unnamed.inject.assisted.ValueFactory;
import me.syncwrld.booter.libs.unnamed.inject.error.ErrorAttachable;
import me.syncwrld.booter.libs.unnamed.inject.key.Key;
import me.syncwrld.booter.libs.unnamed.inject.key.TypeReference;
import me.syncwrld.booter.libs.unnamed.inject.multibinding.CollectionCreator;
import me.syncwrld.booter.libs.unnamed.inject.multibinding.MapCreator;
import me.syncwrld.booter.libs.unnamed.inject.provision.std.generic.GenericProvider;
import me.syncwrld.booter.libs.unnamed.inject.scope.Scope;
import me.syncwrld.booter.libs.unnamed.inject.scope.Scopes;

public interface Binder extends ErrorAttachable {
  @Deprecated
  void $unsafeBind(Key<?> paramKey, Provider<?> paramProvider);
  
  default <T> QualifiedBindingBuilder<T> bind(Class<T> keyType) {
    return bind(TypeReference.of(keyType));
  }
  
  <T> QualifiedBindingBuilder<T> bind(TypeReference<T> paramTypeReference);
  
  default <T> MultiBindingBuilder<T> multibind(Class<T> keyType) {
    return multibind(TypeReference.of(keyType));
  }
  
  <T> MultiBindingBuilder<T> multibind(TypeReference<T> paramTypeReference);
  
  void install(Module... modules) {
    install(Arrays.asList(modules));
  }
  
  void install(Iterable<? extends Module> paramIterable);
  
  public static interface Scoped {
    void in(Scope param1Scope);
    
    default void singleton() {
      in(Scopes.SINGLETON);
    }
  }
  
  public static interface Qualified<R> {
    R markedWith(Class<? extends Annotation> param1Class);
    
    R qualified(Annotation param1Annotation);
    
    R named(String param1String);
  }
  
  public static interface Linked<R, T> {
    default R to(Class<? extends T> targetType) {
      return to(TypeReference.of(targetType));
    }
    
    R to(TypeReference<? extends T> param1TypeReference);
    
    R toProvider(Provider<? extends T> param1Provider);
    
    R toGenericProvider(GenericProvider<? extends T> param1GenericProvider);
    
    default void toFactory(Class<? extends ValueFactory> factory) {
      toFactory(TypeReference.of(factory));
    }
    
    void toFactory(TypeReference<? extends ValueFactory> param1TypeReference);
    
    default <P extends Provider<? extends T>> R toProvider(Class<P> providerClass) {
      return toProvider(TypeReference.of(providerClass));
    }
    
    <P extends Provider<? extends T>> R toProvider(TypeReference<P> param1TypeReference);
  }
  
  public static interface QualifiedBindingBuilder<T> extends Qualified<QualifiedBindingBuilder<T>>, Linked<Scoped, T>, Scoped {
    void toInstance(T param1T);
  }
  
  public static interface MultiBindingBuilder<T> extends Qualified<MultiBindingBuilder<T>> {
    default Binder.CollectionMultiBindingBuilder<T> asSet() {
      return asCollection(Set.class, java.util.HashSet::new);
    }
    
    default Binder.CollectionMultiBindingBuilder<T> asList() {
      return asCollection(List.class, java.util.ArrayList::new);
    }
    
    default Binder.CollectionMultiBindingBuilder<T> asCollection(CollectionCreator collectionCreator) {
      return asCollection(Collection.class, collectionCreator);
    }
    
    Binder.CollectionMultiBindingBuilder<T> asCollection(Class<?> param1Class, CollectionCreator param1CollectionCreator);
    
    default <K> Binder.MapMultiBindingBuilder<K, T> asMap(Class<K> keyClass) {
      return asMap(keyClass, java.util.HashMap::new);
    }
    
    default <K> Binder.MapMultiBindingBuilder<K, T> asMap(Class<K> keyClass, MapCreator mapCreator) {
      return asMap(TypeReference.of(keyClass), mapCreator);
    }
    
    default <K> Binder.MapMultiBindingBuilder<K, T> asMap(TypeReference<K> keyReference) {
      return asMap(keyReference, java.util.HashMap::new);
    }
    
    <K> Binder.MapMultiBindingBuilder<K, T> asMap(TypeReference<K> param1TypeReference, MapCreator param1MapCreator);
  }
  
  public static interface CollectionMultiBindingBuilder<T> extends Linked<CollectionMultiBindingBuilder<T>, T>, Scoped {
    CollectionMultiBindingBuilder<T> toInstance(T param1T);
  }
  
  public static interface MapMultiBindingBuilder<K, V> extends Scoped {
    Binder.KeyBinder<K, V> bind(K param1K);
  }
  
  public static interface KeyBinder<K, V> extends Linked<MapMultiBindingBuilder<K, V>, V> {
    Binder.MapMultiBindingBuilder<K, V> toInstance(V param1V);
  }
}
