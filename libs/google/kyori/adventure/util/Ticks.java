package me.syncwrld.booter.libs.google.kyori.adventure.util;

import java.time.Duration;
import me.syncwrld.booter.libs.jtann.NotNull;

public interface Ticks {
  public static final int TICKS_PER_SECOND = 20;
  
  public static final long SINGLE_TICK_DURATION_MS = 50L;
  
  @NotNull
  static Duration duration(long ticks) {
    return Duration.ofMillis(ticks * 50L);
  }
}
