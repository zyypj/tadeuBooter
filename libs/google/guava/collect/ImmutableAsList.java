package me.syncwrld.booter.libs.google.guava.collect;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible(serializable = true, emulated = true)
abstract class ImmutableAsList<E> extends ImmutableList<E> {
  abstract ImmutableCollection<E> delegateCollection();
  
  public boolean contains(@CheckForNull Object target) {
    return delegateCollection().contains(target);
  }
  
  public int size() {
    return delegateCollection().size();
  }
  
  public boolean isEmpty() {
    return delegateCollection().isEmpty();
  }
  
  boolean isPartialView() {
    return delegateCollection().isPartialView();
  }
  
  @GwtIncompatible
  @J2ktIncompatible
  static class SerializedForm implements Serializable {
    final ImmutableCollection<?> collection;
    
    private static final long serialVersionUID = 0L;
    
    SerializedForm(ImmutableCollection<?> collection) {
      this.collection = collection;
    }
    
    Object readResolve() {
      return this.collection.asList();
    }
  }
  
  @GwtIncompatible
  @J2ktIncompatible
  private void readObject(ObjectInputStream stream) throws InvalidObjectException {
    throw new InvalidObjectException("Use SerializedForm");
  }
  
  @GwtIncompatible
  @J2ktIncompatible
  Object writeReplace() {
    return new SerializedForm(delegateCollection());
  }
}
