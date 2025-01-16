package me.syncwrld.booter.libs.google.guava.util.concurrent;

import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public final class Runnables {
  private static final Runnable EMPTY_RUNNABLE = new Runnable() {
      public void run() {}
    };
  
  public static Runnable doNothing() {
    return EMPTY_RUNNABLE;
  }
}
