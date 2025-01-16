package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Set;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible(serializable = true)
final class JdkBackedImmutableSet<E> extends IndexedImmutableSet<E> {
  private final Set<?> delegate;
  
  private final ImmutableList<E> delegateList;
  
  JdkBackedImmutableSet(Set<?> delegate, ImmutableList<E> delegateList) {
    this.delegate = delegate;
    this.delegateList = delegateList;
  }
  
  E get(int index) {
    return this.delegateList.get(index);
  }
  
  public boolean contains(@CheckForNull Object object) {
    return this.delegate.contains(object);
  }
  
  boolean isPartialView() {
    return false;
  }
  
  public int size() {
    return this.delegateList.size();
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  Object writeReplace() {
    return super.writeReplace();
  }
}
