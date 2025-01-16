package me.syncwrld.booter.libs.google.guava.hash;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import me.syncwrld.booter.libs.google.guava.annotations.Beta;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;

@ElementTypesAreNonnullByDefault
@Beta
public final class HashingOutputStream extends FilterOutputStream {
  private final Hasher hasher;
  
  public HashingOutputStream(HashFunction hashFunction, OutputStream out) {
    super((OutputStream)Preconditions.checkNotNull(out));
    this.hasher = (Hasher)Preconditions.checkNotNull(hashFunction.newHasher());
  }
  
  public void write(int b) throws IOException {
    this.hasher.putByte((byte)b);
    this.out.write(b);
  }
  
  public void write(byte[] bytes, int off, int len) throws IOException {
    this.hasher.putBytes(bytes, off, len);
    this.out.write(bytes, off, len);
  }
  
  public HashCode hash() {
    return this.hasher.hash();
  }
  
  public void close() throws IOException {
    this.out.close();
  }
}
