package me.syncwrld.booter.libs.google.guava.base;

import java.util.function.Function;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@FunctionalInterface
@ElementTypesAreNonnullByDefault
@GwtCompatible
public interface Function<F, T> extends Function<F, T> {
  @ParametricNullness
  T apply(@ParametricNullness F paramF);
  
  boolean equals(@CheckForNull Object paramObject);
}
