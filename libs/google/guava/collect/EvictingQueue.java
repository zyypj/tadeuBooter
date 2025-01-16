package me.syncwrld.booter.libs.google.guava.collect;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Queue;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.VisibleForTesting;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public final class EvictingQueue<E> extends ForwardingQueue<E> implements Serializable {
  private final Queue<E> delegate;
  
  @VisibleForTesting
  final int maxSize;
  
  private static final long serialVersionUID = 0L;
  
  private EvictingQueue(int maxSize) {
    Preconditions.checkArgument((maxSize >= 0), "maxSize (%s) must >= 0", maxSize);
    this.delegate = new ArrayDeque<>(maxSize);
    this.maxSize = maxSize;
  }
  
  public static <E> EvictingQueue<E> create(int maxSize) {
    return new EvictingQueue<>(maxSize);
  }
  
  public int remainingCapacity() {
    return this.maxSize - size();
  }
  
  protected Queue<E> delegate() {
    return this.delegate;
  }
  
  @CanIgnoreReturnValue
  public boolean offer(E e) {
    return add(e);
  }
  
  @CanIgnoreReturnValue
  public boolean add(E e) {
    Preconditions.checkNotNull(e);
    if (this.maxSize == 0)
      return true; 
    if (size() == this.maxSize)
      this.delegate.remove(); 
    this.delegate.add(e);
    return true;
  }
  
  @CanIgnoreReturnValue
  public boolean addAll(Collection<? extends E> collection) {
    int size = collection.size();
    if (size >= this.maxSize) {
      clear();
      return Iterables.addAll(this, Iterables.skip(collection, size - this.maxSize));
    } 
    return standardAddAll(collection);
  }
  
  @J2ktIncompatible
  public Object[] toArray() {
    return super.toArray();
  }
}
