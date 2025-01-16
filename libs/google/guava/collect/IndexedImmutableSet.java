package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;

@ElementTypesAreNonnullByDefault
@GwtCompatible(emulated = true)
abstract class IndexedImmutableSet<E> extends ImmutableSet.CachingAsList<E> {
  public UnmodifiableIterator<E> iterator() {
    return asList().iterator();
  }
  
  public Spliterator<E> spliterator() {
    return CollectSpliterators.indexed(size(), 1297, this::get);
  }
  
  public void forEach(Consumer<? super E> consumer) {
    Preconditions.checkNotNull(consumer);
    int n = size();
    for (int i = 0; i < n; i++)
      consumer.accept(get(i)); 
  }
  
  @GwtIncompatible
  int copyIntoArray(Object[] dst, int offset) {
    return asList().copyIntoArray(dst, offset);
  }
  
  ImmutableList<E> createAsList() {
    return new ImmutableAsList<E>() {
        public E get(int index) {
          return IndexedImmutableSet.this.get(index);
        }
        
        boolean isPartialView() {
          return IndexedImmutableSet.this.isPartialView();
        }
        
        public int size() {
          return IndexedImmutableSet.this.size();
        }
        
        ImmutableCollection<E> delegateCollection() {
          return IndexedImmutableSet.this;
        }
        
        @J2ktIncompatible
        @GwtIncompatible
        Object writeReplace() {
          return super.writeReplace();
        }
      };
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  Object writeReplace() {
    return super.writeReplace();
  }
  
  abstract E get(int paramInt);
}
