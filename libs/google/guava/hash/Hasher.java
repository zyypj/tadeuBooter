package me.syncwrld.booter.libs.google.guava.hash;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.Beta;

@ElementTypesAreNonnullByDefault
@Beta
public interface Hasher extends PrimitiveSink {
  @CanIgnoreReturnValue
  Hasher putByte(byte paramByte);
  
  @CanIgnoreReturnValue
  Hasher putBytes(byte[] paramArrayOfbyte);
  
  @CanIgnoreReturnValue
  Hasher putBytes(byte[] paramArrayOfbyte, int paramInt1, int paramInt2);
  
  @CanIgnoreReturnValue
  Hasher putBytes(ByteBuffer paramByteBuffer);
  
  @CanIgnoreReturnValue
  Hasher putShort(short paramShort);
  
  @CanIgnoreReturnValue
  Hasher putInt(int paramInt);
  
  @CanIgnoreReturnValue
  Hasher putLong(long paramLong);
  
  @CanIgnoreReturnValue
  Hasher putFloat(float paramFloat);
  
  @CanIgnoreReturnValue
  Hasher putDouble(double paramDouble);
  
  @CanIgnoreReturnValue
  Hasher putBoolean(boolean paramBoolean);
  
  @CanIgnoreReturnValue
  Hasher putChar(char paramChar);
  
  @CanIgnoreReturnValue
  Hasher putUnencodedChars(CharSequence paramCharSequence);
  
  @CanIgnoreReturnValue
  Hasher putString(CharSequence paramCharSequence, Charset paramCharset);
  
  @CanIgnoreReturnValue
  <T> Hasher putObject(@ParametricNullness T paramT, Funnel<? super T> paramFunnel);
  
  HashCode hash();
  
  @Deprecated
  int hashCode();
}
