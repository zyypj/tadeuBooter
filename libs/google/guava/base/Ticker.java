package me.syncwrld.booter.libs.google.guava.base;

import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class Ticker {
  public abstract long read();
  
  public static Ticker systemTicker() {
    return SYSTEM_TICKER;
  }
  
  private static final Ticker SYSTEM_TICKER = new Ticker() {
      public long read() {
        return System.nanoTime();
      }
    };
}
