package me.syncwrld.booter.libs.google.guava.util.concurrent;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
public final class Atomics {
  public static <V> AtomicReference<V> newReference() {
    return new AtomicReference<>();
  }
  
  public static <V> AtomicReference<V> newReference(@ParametricNullness V initialValue) {
    return new AtomicReference<>(initialValue);
  }
  
  public static <E> AtomicReferenceArray<E> newReferenceArray(int length) {
    return new AtomicReferenceArray<>(length);
  }
  
  public static <E> AtomicReferenceArray<E> newReferenceArray(E[] array) {
    return new AtomicReferenceArray<>(array);
  }
}
