package me.syncwrld.booter.libs.google.guava.util.concurrent;

import java.util.concurrent.locks.LockSupport;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@J2ktIncompatible
final class OverflowAvoidingLockSupport {
  static final long MAX_NANOSECONDS_THRESHOLD = 2147483647999999999L;
  
  static void parkNanos(@CheckForNull Object blocker, long nanos) {
    LockSupport.parkNanos(blocker, Math.min(nanos, 2147483647999999999L));
  }
}
