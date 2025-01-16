package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;

@ElementTypesAreNonnullByDefault
@GwtCompatible(emulated = true)
final class Platform {
  static <K, V> Map<K, V> newHashMapWithExpectedSize(int expectedSize) {
    return Maps.newHashMapWithExpectedSize(expectedSize);
  }
  
  static <K, V> Map<K, V> newLinkedHashMapWithExpectedSize(int expectedSize) {
    return Maps.newLinkedHashMapWithExpectedSize(expectedSize);
  }
  
  static <E> Set<E> newHashSetWithExpectedSize(int expectedSize) {
    return Sets.newHashSetWithExpectedSize(expectedSize);
  }
  
  static <E> Set<E> newConcurrentHashSet() {
    return ConcurrentHashMap.newKeySet();
  }
  
  static <E> Set<E> newLinkedHashSetWithExpectedSize(int expectedSize) {
    return Sets.newLinkedHashSetWithExpectedSize(expectedSize);
  }
  
  static <K, V> Map<K, V> preservesInsertionOrderOnPutsMap() {
    return Maps.newLinkedHashMap();
  }
  
  static <E> Set<E> preservesInsertionOrderOnAddsSet() {
    return CompactHashSet.create();
  }
  
  static <T> T[] newArray(T[] reference, int length) {
    T[] empty = (reference.length == 0) ? reference : Arrays.<T>copyOf(reference, 0);
    return Arrays.copyOf(empty, length);
  }
  
  static <T> T[] copy(Object[] source, int from, int to, T[] arrayOfType) {
    return Arrays.copyOfRange(source, from, to, (Class)arrayOfType.getClass());
  }
  
  @J2ktIncompatible
  static MapMaker tryWeakKeys(MapMaker mapMaker) {
    return mapMaker.weakKeys();
  }
  
  static <E extends Enum<E>> Class<E> getDeclaringClassOrObjectForJ2cl(E e) {
    return e.getDeclaringClass();
  }
  
  static int reduceIterationsIfGwt(int iterations) {
    return iterations;
  }
  
  static int reduceExponentIfGwt(int exponent) {
    return exponent;
  }
}
