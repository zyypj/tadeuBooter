package me.syncwrld.booter.libs.google.gson.internal;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.RandomAccess;

public class NonNullElementWrapperList<E> extends AbstractList<E> implements RandomAccess {
  private final ArrayList<E> delegate;
  
  public NonNullElementWrapperList(ArrayList<E> delegate) {
    this.delegate = Objects.<ArrayList<E>>requireNonNull(delegate);
  }
  
  public E get(int index) {
    return this.delegate.get(index);
  }
  
  public int size() {
    return this.delegate.size();
  }
  
  private E nonNull(E element) {
    if (element == null)
      throw new NullPointerException("Element must be non-null"); 
    return element;
  }
  
  public E set(int index, E element) {
    return this.delegate.set(index, nonNull(element));
  }
  
  public void add(int index, E element) {
    this.delegate.add(index, nonNull(element));
  }
  
  public E remove(int index) {
    return this.delegate.remove(index);
  }
  
  public void clear() {
    this.delegate.clear();
  }
  
  public boolean remove(Object o) {
    return this.delegate.remove(o);
  }
  
  public boolean removeAll(Collection<?> c) {
    return this.delegate.removeAll(c);
  }
  
  public boolean retainAll(Collection<?> c) {
    return this.delegate.retainAll(c);
  }
  
  public boolean contains(Object o) {
    return this.delegate.contains(o);
  }
  
  public int indexOf(Object o) {
    return this.delegate.indexOf(o);
  }
  
  public int lastIndexOf(Object o) {
    return this.delegate.lastIndexOf(o);
  }
  
  public Object[] toArray() {
    return this.delegate.toArray();
  }
  
  public <T> T[] toArray(T[] a) {
    return this.delegate.toArray(a);
  }
  
  public boolean equals(Object o) {
    return this.delegate.equals(o);
  }
  
  public int hashCode() {
    return this.delegate.hashCode();
  }
}
