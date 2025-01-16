package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Iterator;
import me.syncwrld.booter.libs.google.errorprone.annotations.DoNotCall;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class UnmodifiableIterator<E> implements Iterator<E> {
  @Deprecated
  @DoNotCall("Always throws UnsupportedOperationException")
  public final void remove() {
    throw new UnsupportedOperationException();
  }
}
