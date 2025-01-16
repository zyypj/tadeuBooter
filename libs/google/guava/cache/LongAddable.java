package me.syncwrld.booter.libs.google.guava.cache;

import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;

@ElementTypesAreNonnullByDefault
@GwtCompatible
interface LongAddable {
  void increment();
  
  void add(long paramLong);
  
  long sum();
}
