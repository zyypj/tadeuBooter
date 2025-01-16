package me.syncwrld.booter.libs.google.guava.base;

import java.lang.ref.WeakReference;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
public abstract class FinalizableWeakReference<T> extends WeakReference<T> implements FinalizableReference {
  protected FinalizableWeakReference(@CheckForNull T referent, FinalizableReferenceQueue queue) {
    super(referent, queue.queue);
    queue.cleanUp();
  }
}
