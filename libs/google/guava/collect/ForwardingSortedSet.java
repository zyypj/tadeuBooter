package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class ForwardingSortedSet<E> extends ForwardingSet<E> implements SortedSet<E> {
  @CheckForNull
  public Comparator<? super E> comparator() {
    return delegate().comparator();
  }
  
  @ParametricNullness
  public E first() {
    return delegate().first();
  }
  
  public SortedSet<E> headSet(@ParametricNullness E toElement) {
    return delegate().headSet(toElement);
  }
  
  @ParametricNullness
  public E last() {
    return delegate().last();
  }
  
  public SortedSet<E> subSet(@ParametricNullness E fromElement, @ParametricNullness E toElement) {
    return delegate().subSet(fromElement, toElement);
  }
  
  public SortedSet<E> tailSet(@ParametricNullness E fromElement) {
    return delegate().tailSet(fromElement);
  }
  
  protected boolean standardContains(@CheckForNull Object object) {
    try {
      SortedSet<Object> self = (SortedSet)this;
      Object ceiling = self.tailSet(object).first();
      return (ForwardingSortedMap.unsafeCompare(comparator(), ceiling, object) == 0);
    } catch (ClassCastException|java.util.NoSuchElementException|NullPointerException e) {
      return false;
    } 
  }
  
  protected boolean standardRemove(@CheckForNull Object object) {
    try {
      SortedSet<Object> self = (SortedSet)this;
      Iterator<?> iterator = self.tailSet(object).iterator();
      if (iterator.hasNext()) {
        Object ceiling = iterator.next();
        if (ForwardingSortedMap.unsafeCompare(comparator(), ceiling, object) == 0) {
          iterator.remove();
          return true;
        } 
      } 
    } catch (ClassCastException|NullPointerException e) {
      return false;
    } 
    return false;
  }
  
  protected SortedSet<E> standardSubSet(@ParametricNullness E fromElement, @ParametricNullness E toElement) {
    return tailSet(fromElement).headSet(toElement);
  }
  
  protected abstract SortedSet<E> delegate();
}
