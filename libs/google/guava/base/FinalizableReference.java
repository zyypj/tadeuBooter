package me.syncwrld.booter.libs.google.guava.base;

import me.syncwrld.booter.libs.google.errorprone.annotations.DoNotMock;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;

@DoNotMock("Use an instance of one of the Finalizable*Reference classes")
@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
public interface FinalizableReference {
  void finalizeReferent();
}
