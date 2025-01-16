package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Iterator;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible(serializable = true, emulated = true)
final class SingletonImmutableSet<E> extends ImmutableSet<E> {
  final transient E element;
  
  SingletonImmutableSet(E element) {
    this.element = (E)Preconditions.checkNotNull(element);
  }
  
  public int size() {
    return 1;
  }
  
  public boolean contains(@CheckForNull Object target) {
    return this.element.equals(target);
  }
  
  public UnmodifiableIterator<E> iterator() {
    return Iterators.singletonIterator(this.element);
  }
  
  public ImmutableList<E> asList() {
    return ImmutableList.of(this.element);
  }
  
  boolean isPartialView() {
    return false;
  }
  
  int copyIntoArray(Object[] dst, int offset) {
    dst[offset] = this.element;
    return offset + 1;
  }
  
  public final int hashCode() {
    return this.element.hashCode();
  }
  
  public String toString() {
    return '[' + this.element.toString() + ']';
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  Object writeReplace() {
    return super.writeReplace();
  }
}
