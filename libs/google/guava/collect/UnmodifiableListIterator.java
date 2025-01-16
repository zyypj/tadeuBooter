package me.syncwrld.booter.libs.google.guava.collect;

import java.util.ListIterator;
import me.syncwrld.booter.libs.google.errorprone.annotations.DoNotCall;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class UnmodifiableListIterator<E> extends UnmodifiableIterator<E> implements ListIterator<E> {
  @Deprecated
  @DoNotCall("Always throws UnsupportedOperationException")
  public final void add(@ParametricNullness E e) {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  @DoNotCall("Always throws UnsupportedOperationException")
  public final void set(@ParametricNullness E e) {
    throw new UnsupportedOperationException();
  }
}
