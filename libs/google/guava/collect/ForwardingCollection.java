package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Collection;
import java.util.Iterator;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.base.Objects;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class ForwardingCollection<E> extends ForwardingObject implements Collection<E> {
  public Iterator<E> iterator() {
    return delegate().iterator();
  }
  
  public int size() {
    return delegate().size();
  }
  
  @CanIgnoreReturnValue
  public boolean removeAll(Collection<?> collection) {
    return delegate().removeAll(collection);
  }
  
  public boolean isEmpty() {
    return delegate().isEmpty();
  }
  
  public boolean contains(@CheckForNull Object object) {
    return delegate().contains(object);
  }
  
  @CanIgnoreReturnValue
  public boolean add(@ParametricNullness E element) {
    return delegate().add(element);
  }
  
  @CanIgnoreReturnValue
  public boolean remove(@CheckForNull Object object) {
    return delegate().remove(object);
  }
  
  public boolean containsAll(Collection<?> collection) {
    return delegate().containsAll(collection);
  }
  
  @CanIgnoreReturnValue
  public boolean addAll(Collection<? extends E> collection) {
    return delegate().addAll(collection);
  }
  
  @CanIgnoreReturnValue
  public boolean retainAll(Collection<?> collection) {
    return delegate().retainAll(collection);
  }
  
  public void clear() {
    delegate().clear();
  }
  
  public Object[] toArray() {
    return delegate().toArray();
  }
  
  @CanIgnoreReturnValue
  public <T> T[] toArray(T[] array) {
    return delegate().toArray(array);
  }
  
  protected boolean standardContains(@CheckForNull Object object) {
    return Iterators.contains(iterator(), object);
  }
  
  protected boolean standardContainsAll(Collection<?> collection) {
    return Collections2.containsAllImpl(this, collection);
  }
  
  protected boolean standardAddAll(Collection<? extends E> collection) {
    return Iterators.addAll(this, collection.iterator());
  }
  
  protected boolean standardRemove(@CheckForNull Object object) {
    Iterator<E> iterator = iterator();
    while (iterator.hasNext()) {
      if (Objects.equal(iterator.next(), object)) {
        iterator.remove();
        return true;
      } 
    } 
    return false;
  }
  
  protected boolean standardRemoveAll(Collection<?> collection) {
    return Iterators.removeAll(iterator(), collection);
  }
  
  protected boolean standardRetainAll(Collection<?> collection) {
    return Iterators.retainAll(iterator(), collection);
  }
  
  protected void standardClear() {
    Iterators.clear(iterator());
  }
  
  protected boolean standardIsEmpty() {
    return !iterator().hasNext();
  }
  
  protected String standardToString() {
    return Collections2.toStringImpl(this);
  }
  
  protected Object[] standardToArray() {
    Object[] newArray = new Object[size()];
    return toArray(newArray);
  }
  
  protected <T> T[] standardToArray(T[] array) {
    return ObjectArrays.toArrayImpl(this, array);
  }
  
  protected abstract Collection<E> delegate();
}
