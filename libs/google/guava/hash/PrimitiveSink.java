package me.syncwrld.booter.libs.google.guava.hash;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.Beta;

@ElementTypesAreNonnullByDefault
@Beta
public interface PrimitiveSink {
  @CanIgnoreReturnValue
  PrimitiveSink putByte(byte paramByte);
  
  @CanIgnoreReturnValue
  PrimitiveSink putBytes(byte[] paramArrayOfbyte);
  
  @CanIgnoreReturnValue
  PrimitiveSink putBytes(byte[] paramArrayOfbyte, int paramInt1, int paramInt2);
  
  @CanIgnoreReturnValue
  PrimitiveSink putBytes(ByteBuffer paramByteBuffer);
  
  @CanIgnoreReturnValue
  PrimitiveSink putShort(short paramShort);
  
  @CanIgnoreReturnValue
  PrimitiveSink putInt(int paramInt);
  
  @CanIgnoreReturnValue
  PrimitiveSink putLong(long paramLong);
  
  @CanIgnoreReturnValue
  PrimitiveSink putFloat(float paramFloat);
  
  @CanIgnoreReturnValue
  PrimitiveSink putDouble(double paramDouble);
  
  @CanIgnoreReturnValue
  PrimitiveSink putBoolean(boolean paramBoolean);
  
  @CanIgnoreReturnValue
  PrimitiveSink putChar(char paramChar);
  
  @CanIgnoreReturnValue
  PrimitiveSink putUnencodedChars(CharSequence paramCharSequence);
  
  @CanIgnoreReturnValue
  PrimitiveSink putString(CharSequence paramCharSequence, Charset paramCharset);
}
