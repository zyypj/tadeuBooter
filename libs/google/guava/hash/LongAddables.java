package me.syncwrld.booter.libs.google.guava.hash;

import java.util.concurrent.atomic.AtomicLong;
import me.syncwrld.booter.libs.google.guava.base.Supplier;

@ElementTypesAreNonnullByDefault
final class LongAddables {
  private static final Supplier<LongAddable> SUPPLIER;
  
  static {
    Supplier<LongAddable> supplier;
    try {
      LongAdder unused = new LongAdder();
      supplier = new Supplier<LongAddable>() {
          public LongAddable get() {
            return new LongAdder();
          }
        };
    } catch (Throwable t) {
      supplier = new Supplier<LongAddable>() {
          public LongAddable get() {
            return new LongAddables.PureJavaLongAddable();
          }
        };
    } 
    SUPPLIER = supplier;
  }
  
  public static LongAddable create() {
    return (LongAddable)SUPPLIER.get();
  }
  
  private static final class PureJavaLongAddable extends AtomicLong implements LongAddable {
    private PureJavaLongAddable() {}
    
    public void increment() {
      getAndIncrement();
    }
    
    public void add(long x) {
      getAndAdd(x);
    }
    
    public long sum() {
      return get();
    }
  }
}
