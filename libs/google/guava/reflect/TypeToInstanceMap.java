package me.syncwrld.booter.libs.google.guava.reflect;

import java.util.Map;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.errorprone.annotations.DoNotMock;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@DoNotMock("Use ImmutableTypeToInstanceMap or MutableTypeToInstanceMap")
@ElementTypesAreNonnullByDefault
public interface TypeToInstanceMap<B> extends Map<TypeToken<? extends B>, B> {
  @CheckForNull
  <T extends B> T getInstance(Class<T> paramClass);
  
  @CheckForNull
  <T extends B> T getInstance(TypeToken<T> paramTypeToken);
  
  @CheckForNull
  @CanIgnoreReturnValue
  <T extends B> T putInstance(Class<T> paramClass, @ParametricNullness T paramT);
  
  @CheckForNull
  @CanIgnoreReturnValue
  <T extends B> T putInstance(TypeToken<T> paramTypeToken, @ParametricNullness T paramT);
}
