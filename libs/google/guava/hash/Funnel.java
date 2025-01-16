package me.syncwrld.booter.libs.google.guava.hash;

import java.io.Serializable;
import me.syncwrld.booter.libs.google.errorprone.annotations.DoNotMock;
import me.syncwrld.booter.libs.google.guava.annotations.Beta;

@DoNotMock("Implement with a lambda")
@ElementTypesAreNonnullByDefault
@Beta
public interface Funnel<T> extends Serializable {
  void funnel(@ParametricNullness T paramT, PrimitiveSink paramPrimitiveSink);
}
