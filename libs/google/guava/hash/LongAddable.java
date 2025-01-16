package me.syncwrld.booter.libs.google.guava.hash;

@ElementTypesAreNonnullByDefault
interface LongAddable {
  void increment();
  
  void add(long paramLong);
  
  long sum();
}
