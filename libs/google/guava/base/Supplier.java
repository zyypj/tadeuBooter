package me.syncwrld.booter.libs.google.guava.base;

import java.util.function.Supplier;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;

@FunctionalInterface
@ElementTypesAreNonnullByDefault
@GwtCompatible
public interface Supplier<T> extends Supplier<T> {
  @ParametricNullness
  T get();
}
