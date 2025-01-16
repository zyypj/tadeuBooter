package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class ForwardingList<E> extends ForwardingCollection<E> implements List<E> {
  public void add(int index, @ParametricNullness E element) {
    delegate().add(index, element);
  }
  
  @CanIgnoreReturnValue
  public boolean addAll(int index, Collection<? extends E> elements) {
    return delegate().addAll(index, elements);
  }
  
  @ParametricNullness
  public E get(int index) {
    return delegate().get(index);
  }
  
  public int indexOf(@CheckForNull Object element) {
    return delegate().indexOf(element);
  }
  
  public int lastIndexOf(@CheckForNull Object element) {
    return delegate().lastIndexOf(element);
  }
  
  public ListIterator<E> listIterator() {
    return delegate().listIterator();
  }
  
  public ListIterator<E> listIterator(int index) {
    return delegate().listIterator(index);
  }
  
  @ParametricNullness
  @CanIgnoreReturnValue
  public E remove(int index) {
    return delegate().remove(index);
  }
  
  @ParametricNullness
  @CanIgnoreReturnValue
  public E set(int index, @ParametricNullness E element) {
    return delegate().set(index, element);
  }
  
  public List<E> subList(int fromIndex, int toIndex) {
    return delegate().subList(fromIndex, toIndex);
  }
  
  public boolean equals(@CheckForNull Object object) {
    return (object == this || delegate().equals(object));
  }
  
  public int hashCode() {
    return delegate().hashCode();
  }
  
  protected boolean standardAdd(@ParametricNullness E element) {
    add(size(), element);
    return true;
  }
  
  protected boolean standardAddAll(int index, Iterable<? extends E> elements) {
    return Lists.addAllImpl(this, index, elements);
  }
  
  protected int standardIndexOf(@CheckForNull Object element) {
    return Lists.indexOfImpl(this, element);
  }
  
  protected int standardLastIndexOf(@CheckForNull Object element) {
    return Lists.lastIndexOfImpl(this, element);
  }
  
  protected Iterator<E> standardIterator() {
    return listIterator();
  }
  
  protected ListIterator<E> standardListIterator() {
    return listIterator(0);
  }
  
  protected ListIterator<E> standardListIterator(int start) {
    return Lists.listIteratorImpl(this, start);
  }
  
  protected List<E> standardSubList(int fromIndex, int toIndex) {
    return Lists.subListImpl(this, fromIndex, toIndex);
  }
  
  protected boolean standardEquals(@CheckForNull Object object) {
    return Lists.equalsImpl(this, object);
  }
  
  protected int standardHashCode() {
    return Lists.hashCodeImpl(this);
  }
  
  protected abstract List<E> delegate();
}
