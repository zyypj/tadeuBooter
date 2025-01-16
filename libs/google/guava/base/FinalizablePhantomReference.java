package me.syncwrld.booter.libs.google.guava.base;

import java.lang.ref.PhantomReference;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
public abstract class FinalizablePhantomReference<T> extends PhantomReference<T> implements FinalizableReference {
  protected FinalizablePhantomReference(@CheckForNull T referent, FinalizableReferenceQueue queue) {
    super(referent, queue.queue);
    queue.cleanUp();
  }
}
