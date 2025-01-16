package me.syncwrld.booter.libs.google.guava.util.concurrent;

import java.time.Duration;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;

@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
final class Internal {
  static long toNanosSaturated(Duration duration) {
    try {
      return duration.toNanos();
    } catch (ArithmeticException tooBig) {
      return duration.isNegative() ? Long.MIN_VALUE : Long.MAX_VALUE;
    } 
  }
}
