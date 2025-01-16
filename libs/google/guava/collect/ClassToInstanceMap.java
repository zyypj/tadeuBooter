package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Map;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.errorprone.annotations.DoNotMock;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@DoNotMock("Use ImmutableClassToInstanceMap or MutableClassToInstanceMap")
@ElementTypesAreNonnullByDefault
@GwtCompatible
public interface ClassToInstanceMap<B> extends Map<Class<? extends B>, B> {
  @CheckForNull
  <T extends B> T getInstance(Class<T> paramClass);
  
  @CheckForNull
  @CanIgnoreReturnValue
  <T extends B> T putInstance(Class<T> paramClass, @ParametricNullness T paramT);
}
