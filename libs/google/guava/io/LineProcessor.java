package me.syncwrld.booter.libs.google.guava.io;

import java.io.IOException;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;

@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
public interface LineProcessor<T> {
  @CanIgnoreReturnValue
  boolean processLine(String paramString) throws IOException;
  
  @ParametricNullness
  T getResult();
}
