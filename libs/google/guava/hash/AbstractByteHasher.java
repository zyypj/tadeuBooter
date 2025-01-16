package me.syncwrld.booter.libs.google.guava.hash;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;

@ElementTypesAreNonnullByDefault
abstract class AbstractByteHasher extends AbstractHasher {
  private final ByteBuffer scratch = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
  
  protected void update(byte[] b) {
    update(b, 0, b.length);
  }
  
  protected void update(byte[] b, int off, int len) {
    for (int i = off; i < off + len; i++)
      update(b[i]); 
  }
  
  protected void update(ByteBuffer b) {
    if (b.hasArray()) {
      update(b.array(), b.arrayOffset() + b.position(), b.remaining());
      Java8Compatibility.position(b, b.limit());
    } else {
      for (int remaining = b.remaining(); remaining > 0; remaining--)
        update(b.get()); 
    } 
  }
  
  @CanIgnoreReturnValue
  private Hasher update(int bytes) {
    try {
      update(this.scratch.array(), 0, bytes);
    } finally {
      Java8Compatibility.clear(this.scratch);
    } 
    return this;
  }
  
  @CanIgnoreReturnValue
  public Hasher putByte(byte b) {
    update(b);
    return this;
  }
  
  @CanIgnoreReturnValue
  public Hasher putBytes(byte[] bytes) {
    Preconditions.checkNotNull(bytes);
    update(bytes);
    return this;
  }
  
  @CanIgnoreReturnValue
  public Hasher putBytes(byte[] bytes, int off, int len) {
    Preconditions.checkPositionIndexes(off, off + len, bytes.length);
    update(bytes, off, len);
    return this;
  }
  
  @CanIgnoreReturnValue
  public Hasher putBytes(ByteBuffer bytes) {
    update(bytes);
    return this;
  }
  
  @CanIgnoreReturnValue
  public Hasher putShort(short s) {
    this.scratch.putShort(s);
    return update(2);
  }
  
  @CanIgnoreReturnValue
  public Hasher putInt(int i) {
    this.scratch.putInt(i);
    return update(4);
  }
  
  @CanIgnoreReturnValue
  public Hasher putLong(long l) {
    this.scratch.putLong(l);
    return update(8);
  }
  
  @CanIgnoreReturnValue
  public Hasher putChar(char c) {
    this.scratch.putChar(c);
    return update(2);
  }
  
  protected abstract void update(byte paramByte);
}
