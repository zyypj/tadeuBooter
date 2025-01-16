package me.syncwrld.booter.libs.google.guava.hash;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;

@ElementTypesAreNonnullByDefault
abstract class AbstractHasher implements Hasher {
  @CanIgnoreReturnValue
  public final Hasher putBoolean(boolean b) {
    return putByte(b ? 1 : 0);
  }
  
  @CanIgnoreReturnValue
  public final Hasher putDouble(double d) {
    return putLong(Double.doubleToRawLongBits(d));
  }
  
  @CanIgnoreReturnValue
  public final Hasher putFloat(float f) {
    return putInt(Float.floatToRawIntBits(f));
  }
  
  @CanIgnoreReturnValue
  public Hasher putUnencodedChars(CharSequence charSequence) {
    for (int i = 0, len = charSequence.length(); i < len; i++)
      putChar(charSequence.charAt(i)); 
    return this;
  }
  
  @CanIgnoreReturnValue
  public Hasher putString(CharSequence charSequence, Charset charset) {
    return putBytes(charSequence.toString().getBytes(charset));
  }
  
  @CanIgnoreReturnValue
  public Hasher putBytes(byte[] bytes) {
    return putBytes(bytes, 0, bytes.length);
  }
  
  @CanIgnoreReturnValue
  public Hasher putBytes(byte[] bytes, int off, int len) {
    Preconditions.checkPositionIndexes(off, off + len, bytes.length);
    for (int i = 0; i < len; i++)
      putByte(bytes[off + i]); 
    return this;
  }
  
  @CanIgnoreReturnValue
  public Hasher putBytes(ByteBuffer b) {
    if (b.hasArray()) {
      putBytes(b.array(), b.arrayOffset() + b.position(), b.remaining());
      Java8Compatibility.position(b, b.limit());
    } else {
      for (int remaining = b.remaining(); remaining > 0; remaining--)
        putByte(b.get()); 
    } 
    return this;
  }
  
  @CanIgnoreReturnValue
  public Hasher putShort(short s) {
    putByte((byte)s);
    putByte((byte)(s >>> 8));
    return this;
  }
  
  @CanIgnoreReturnValue
  public Hasher putInt(int i) {
    putByte((byte)i);
    putByte((byte)(i >>> 8));
    putByte((byte)(i >>> 16));
    putByte((byte)(i >>> 24));
    return this;
  }
  
  @CanIgnoreReturnValue
  public Hasher putLong(long l) {
    for (int i = 0; i < 64; i += 8)
      putByte((byte)(int)(l >>> i)); 
    return this;
  }
  
  @CanIgnoreReturnValue
  public Hasher putChar(char c) {
    putByte((byte)c);
    putByte((byte)(c >>> 8));
    return this;
  }
  
  @CanIgnoreReturnValue
  public <T> Hasher putObject(@ParametricNullness T instance, Funnel<? super T> funnel) {
    funnel.funnel(instance, this);
    return this;
  }
}
