package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;

@ElementTypesAreNonnullByDefault
@GwtCompatible(serializable = true, emulated = true)
final class SingletonImmutableList<E> extends ImmutableList<E> {
  final transient E element;
  
  SingletonImmutableList(E element) {
    this.element = (E)Preconditions.checkNotNull(element);
  }
  
  public E get(int index) {
    Preconditions.checkElementIndex(index, 1);
    return this.element;
  }
  
  public UnmodifiableIterator<E> iterator() {
    return Iterators.singletonIterator(this.element);
  }
  
  public Spliterator<E> spliterator() {
    return Collections.<E>singleton(this.element).spliterator();
  }
  
  public int size() {
    return 1;
  }
  
  public ImmutableList<E> subList(int fromIndex, int toIndex) {
    Preconditions.checkPositionIndexes(fromIndex, toIndex, 1);
    return (fromIndex == toIndex) ? ImmutableList.<E>of() : this;
  }
  
  public String toString() {
    return '[' + this.element.toString() + ']';
  }
  
  boolean isPartialView() {
    return false;
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  Object writeReplace() {
    return super.writeReplace();
  }
}
