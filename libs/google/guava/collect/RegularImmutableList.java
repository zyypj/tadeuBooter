package me.syncwrld.booter.libs.google.guava.collect;

import java.util.ListIterator;
import java.util.Spliterator;
import java.util.Spliterators;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.VisibleForTesting;

@ElementTypesAreNonnullByDefault
@GwtCompatible(serializable = true, emulated = true)
class RegularImmutableList<E> extends ImmutableList<E> {
  static final ImmutableList<Object> EMPTY = new RegularImmutableList(new Object[0]);
  
  @VisibleForTesting
  final transient Object[] array;
  
  RegularImmutableList(Object[] array) {
    this.array = array;
  }
  
  public int size() {
    return this.array.length;
  }
  
  boolean isPartialView() {
    return false;
  }
  
  Object[] internalArray() {
    return this.array;
  }
  
  int internalArrayStart() {
    return 0;
  }
  
  int internalArrayEnd() {
    return this.array.length;
  }
  
  int copyIntoArray(Object[] dst, int dstOff) {
    System.arraycopy(this.array, 0, dst, dstOff, this.array.length);
    return dstOff + this.array.length;
  }
  
  public E get(int index) {
    return (E)this.array[index];
  }
  
  public UnmodifiableListIterator<E> listIterator(int index) {
    return Iterators.forArrayWithPosition((E[])this.array, index);
  }
  
  public Spliterator<E> spliterator() {
    return Spliterators.spliterator(this.array, 1296);
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  Object writeReplace() {
    return super.writeReplace();
  }
}
