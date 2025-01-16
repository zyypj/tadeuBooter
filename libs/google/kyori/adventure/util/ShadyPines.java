package me.syncwrld.booter.libs.google.kyori.adventure.util;

import java.util.Set;
import me.syncwrld.booter.libs.jtann.ApiStatus.ScheduledForRemoval;
import me.syncwrld.booter.libs.jtann.NotNull;

public final class ShadyPines {
  @Deprecated
  @SafeVarargs
  @ScheduledForRemoval(inVersion = "5.0.0")
  @NotNull
  public static <E extends Enum<E>> Set<E> enumSet(Class<E> type, E... constants) {
    return MonkeyBars.enumSet(type, constants);
  }
  
  public static boolean equals(double a, double b) {
    return (Double.doubleToLongBits(a) == Double.doubleToLongBits(b));
  }
  
  public static boolean equals(float a, float b) {
    return (Float.floatToIntBits(a) == Float.floatToIntBits(b));
  }
}
