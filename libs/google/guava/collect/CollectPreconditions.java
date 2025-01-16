package me.syncwrld.booter.libs.google.guava.collect;

import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;

@ElementTypesAreNonnullByDefault
@GwtCompatible
final class CollectPreconditions {
  static void checkEntryNotNull(Object key, Object value) {
    if (key == null)
      throw new NullPointerException("null key in entry: null=" + value); 
    if (value == null)
      throw new NullPointerException("null value in entry: " + key + "=null"); 
  }
  
  @CanIgnoreReturnValue
  static int checkNonnegative(int value, String name) {
    if (value < 0)
      throw new IllegalArgumentException(name + " cannot be negative but was: " + value); 
    return value;
  }
  
  @CanIgnoreReturnValue
  static long checkNonnegative(long value, String name) {
    if (value < 0L)
      throw new IllegalArgumentException(name + " cannot be negative but was: " + value); 
    return value;
  }
  
  static void checkPositive(int value, String name) {
    if (value <= 0)
      throw new IllegalArgumentException(name + " must be positive but was: " + value); 
  }
  
  static void checkRemove(boolean canRemove) {
    Preconditions.checkState(canRemove, "no calls to next() since the last call to remove()");
  }
}
