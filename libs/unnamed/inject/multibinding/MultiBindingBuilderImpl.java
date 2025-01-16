package me.syncwrld.booter.libs.unnamed.inject.multibinding;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import me.syncwrld.booter.libs.unnamed.inject.Binder;
import me.syncwrld.booter.libs.unnamed.inject.impl.BinderImpl;
import me.syncwrld.booter.libs.unnamed.inject.impl.KeyBuilder;
import me.syncwrld.booter.libs.unnamed.inject.key.Key;
import me.syncwrld.booter.libs.unnamed.inject.key.TypeReference;

public class MultiBindingBuilderImpl<T> implements Binder.MultiBindingBuilder<T>, KeyBuilder<Binder.MultiBindingBuilder<T>, T> {
  private final BinderImpl binder;
  
  private Key<T> key;
  
  public MultiBindingBuilderImpl(BinderImpl binder, TypeReference<T> key) {
    this.key = Key.of(key);
    this.binder = binder;
  }
  
  public Binder.CollectionMultiBindingBuilder<T> asCollection(Class<?> baseType, CollectionCreator collectionCreator) {
    Key<List<T>> listKey = this.key.withType(TypeReference.of(baseType, new Type[] { this.key.getType().getType() }));
    return new CollectionMultiBindingBuilderImpl<>(this.binder, (Key)listKey, this.key, collectionCreator);
  }
  
  public <K> Binder.MapMultiBindingBuilder<K, T> asMap(TypeReference<K> keyReference, MapCreator mapCreator) {
    Key<Map<K, T>> mapKey = this.key.withType(TypeReference.mapTypeOf(keyReference, this.key.getType()));
    return new MapMultiBindingBuilderImpl<>(this.binder, mapCreator, mapKey, this.key);
  }
  
  public Key<T> key() {
    return this.key;
  }
  
  public void setKey(Key<T> key) {
    this.key = key;
  }
  
  public Binder.MultiBindingBuilder<T> getReturnValue() {
    return this;
  }
}
