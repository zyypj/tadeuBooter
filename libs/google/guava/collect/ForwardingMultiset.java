package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.base.Objects;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class ForwardingMultiset<E> extends ForwardingCollection<E> implements Multiset<E> {
  public int count(@CheckForNull Object element) {
    return delegate().count(element);
  }
  
  @CanIgnoreReturnValue
  public int add(@ParametricNullness E element, int occurrences) {
    return delegate().add(element, occurrences);
  }
  
  @CanIgnoreReturnValue
  public int remove(@CheckForNull Object element, int occurrences) {
    return delegate().remove(element, occurrences);
  }
  
  public Set<E> elementSet() {
    return delegate().elementSet();
  }
  
  public Set<Multiset.Entry<E>> entrySet() {
    return delegate().entrySet();
  }
  
  public boolean equals(@CheckForNull Object object) {
    return (object == this || delegate().equals(object));
  }
  
  public int hashCode() {
    return delegate().hashCode();
  }
  
  @CanIgnoreReturnValue
  public int setCount(@ParametricNullness E element, int count) {
    return delegate().setCount(element, count);
  }
  
  @CanIgnoreReturnValue
  public boolean setCount(@ParametricNullness E element, int oldCount, int newCount) {
    return delegate().setCount(element, oldCount, newCount);
  }
  
  protected boolean standardContains(@CheckForNull Object object) {
    return (count(object) > 0);
  }
  
  protected void standardClear() {
    Iterators.clear(entrySet().iterator());
  }
  
  protected int standardCount(@CheckForNull Object object) {
    for (Multiset.Entry<?> entry : entrySet()) {
      if (Objects.equal(entry.getElement(), object))
        return entry.getCount(); 
    } 
    return 0;
  }
  
  protected boolean standardAdd(@ParametricNullness E element) {
    add(element, 1);
    return true;
  }
  
  protected boolean standardAddAll(Collection<? extends E> elementsToAdd) {
    return Multisets.addAllImpl(this, elementsToAdd);
  }
  
  protected boolean standardRemove(@CheckForNull Object element) {
    return (remove(element, 1) > 0);
  }
  
  protected boolean standardRemoveAll(Collection<?> elementsToRemove) {
    return Multisets.removeAllImpl(this, elementsToRemove);
  }
  
  protected boolean standardRetainAll(Collection<?> elementsToRetain) {
    return Multisets.retainAllImpl(this, elementsToRetain);
  }
  
  protected int standardSetCount(@ParametricNullness E element, int count) {
    return Multisets.setCountImpl(this, element, count);
  }
  
  protected boolean standardSetCount(@ParametricNullness E element, int oldCount, int newCount) {
    return Multisets.setCountImpl(this, element, oldCount, newCount);
  }
  
  protected class StandardElementSet extends Multisets.ElementSet<E> {
    Multiset<E> multiset() {
      return ForwardingMultiset.this;
    }
    
    public Iterator<E> iterator() {
      return Multisets.elementIterator(multiset().entrySet().iterator());
    }
  }
  
  protected Iterator<E> standardIterator() {
    return Multisets.iteratorImpl(this);
  }
  
  protected int standardSize() {
    return Multisets.linearTimeSizeImpl(this);
  }
  
  protected boolean standardEquals(@CheckForNull Object object) {
    return Multisets.equalsImpl(this, object);
  }
  
  protected int standardHashCode() {
    return entrySet().hashCode();
  }
  
  protected String standardToString() {
    return entrySet().toString();
  }
  
  protected abstract Multiset<E> delegate();
}
