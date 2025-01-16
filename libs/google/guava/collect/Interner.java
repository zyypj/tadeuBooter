package me.syncwrld.booter.libs.google.guava.collect;

import me.syncwrld.booter.libs.google.errorprone.annotations.DoNotMock;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;

@DoNotMock("Use Interners.new*Interner")
@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
public interface Interner<E> {
  E intern(E paramE);
}
