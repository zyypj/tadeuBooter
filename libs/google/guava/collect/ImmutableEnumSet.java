package me.syncwrld.booter.libs.google.guava.collect;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import me.syncwrld.booter.libs.google.errorprone.annotations.concurrent.LazyInit;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible(serializable = true, emulated = true)
final class ImmutableEnumSet<E extends Enum<E>> extends ImmutableSet<E> {
  private final transient EnumSet<E> delegate;
  
  @LazyInit
  private transient int hashCode;
  
  static <E extends Enum<E>> ImmutableSet<E> asImmutable(EnumSet<E> set) {
    switch (set.size()) {
      case 0:
        return ImmutableSet.of();
      case 1:
        return ImmutableSet.of((E)Iterables.<Enum>getOnlyElement(set));
    } 
    return new ImmutableEnumSet<>(set);
  }
  
  private ImmutableEnumSet(EnumSet<E> delegate) {
    this.delegate = delegate;
  }
  
  boolean isPartialView() {
    return false;
  }
  
  public UnmodifiableIterator<E> iterator() {
    return Iterators.unmodifiableIterator(this.delegate.iterator());
  }
  
  public Spliterator<E> spliterator() {
    return this.delegate.spliterator();
  }
  
  public void forEach(Consumer<? super E> action) {
    this.delegate.forEach(action);
  }
  
  public int size() {
    return this.delegate.size();
  }
  
  public boolean contains(@CheckForNull Object object) {
    return this.delegate.contains(object);
  }
  
  public boolean containsAll(Collection<?> collection) {
    if (collection instanceof ImmutableEnumSet)
      collection = ((ImmutableEnumSet)collection).delegate; 
    return this.delegate.containsAll(collection);
  }
  
  public boolean isEmpty() {
    return this.delegate.isEmpty();
  }
  
  public boolean equals(@CheckForNull Object<E> object) {
    if (object == this)
      return true; 
    if (object instanceof ImmutableEnumSet)
      object = (Object<E>)((ImmutableEnumSet)object).delegate; 
    return this.delegate.equals(object);
  }
  
  boolean isHashCodeFast() {
    return true;
  }
  
  public int hashCode() {
    int result = this.hashCode;
    return (result == 0) ? (this.hashCode = this.delegate.hashCode()) : result;
  }
  
  public String toString() {
    return this.delegate.toString();
  }
  
  @J2ktIncompatible
  Object writeReplace() {
    return new EnumSerializedForm<>(this.delegate);
  }
  
  @J2ktIncompatible
  private void readObject(ObjectInputStream stream) throws InvalidObjectException {
    throw new InvalidObjectException("Use SerializedForm");
  }
  
  @J2ktIncompatible
  private static class EnumSerializedForm<E extends Enum<E>> implements Serializable {
    final EnumSet<E> delegate;
    
    private static final long serialVersionUID = 0L;
    
    EnumSerializedForm(EnumSet<E> delegate) {
      this.delegate = delegate;
    }
    
    Object readResolve() {
      return new ImmutableEnumSet<>(this.delegate.clone());
    }
  }
}
