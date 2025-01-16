package me.syncwrld.booter.libs.google.guava.io;

import java.io.IOException;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.errorprone.annotations.DoNotMock;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;

@DoNotMock("Implement it normally")
@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
public interface ByteProcessor<T> {
  @CanIgnoreReturnValue
  boolean processBytes(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException;
  
  @ParametricNullness
  T getResult();
}
