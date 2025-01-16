package me.syncwrld.booter.libs.google.guava.base;

import java.util.function.Predicate;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@FunctionalInterface
@ElementTypesAreNonnullByDefault
@GwtCompatible
public interface Predicate<T> extends Predicate<T> {
  boolean apply(@ParametricNullness T paramT);
  
  boolean equals(@CheckForNull Object paramObject);
  
  default boolean test(@ParametricNullness T input) {
    return apply(input);
  }
}
