package me.syncwrld.booter.libs.google.guava.util.concurrent;

import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public interface FutureCallback<V> {
  void onSuccess(@ParametricNullness V paramV);
  
  void onFailure(Throwable paramThrowable);
}
