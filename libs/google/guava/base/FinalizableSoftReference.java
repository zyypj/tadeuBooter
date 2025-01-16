package me.syncwrld.booter.libs.google.guava.base;

import java.lang.ref.SoftReference;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
public abstract class FinalizableSoftReference<T> extends SoftReference<T> implements FinalizableReference {
  protected FinalizableSoftReference(@CheckForNull T referent, FinalizableReferenceQueue queue) {
    super(referent, queue.queue);
    queue.cleanUp();
  }
}
