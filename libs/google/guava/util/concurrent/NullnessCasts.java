package me.syncwrld.booter.libs.google.guava.util.concurrent;

import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
final class NullnessCasts {
  @ParametricNullness
  static <T> T uncheckedCastNullableTToT(@CheckForNull T t) {
    return t;
  }
  
  @ParametricNullness
  static <T> T uncheckedNull() {
    return null;
  }
}
