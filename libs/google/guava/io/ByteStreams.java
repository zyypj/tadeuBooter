package me.syncwrld.booter.libs.google.guava.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.guava.math.IntMath;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
public final class ByteStreams {
  private static final int BUFFER_SIZE = 8192;
  
  private static final int ZERO_COPY_CHUNK_SIZE = 524288;
  
  private static final int MAX_ARRAY_LEN = 2147483639;
  
  private static final int TO_BYTE_ARRAY_DEQUE_SIZE = 20;
  
  static byte[] createBuffer() {
    return new byte[8192];
  }
  
  @CanIgnoreReturnValue
  public static long copy(InputStream from, OutputStream to) throws IOException {
    Preconditions.checkNotNull(from);
    Preconditions.checkNotNull(to);
    byte[] buf = createBuffer();
    long total = 0L;
    while (true) {
      int r = from.read(buf);
      if (r == -1)
        break; 
      to.write(buf, 0, r);
      total += r;
    } 
    return total;
  }
  
  @CanIgnoreReturnValue
  public static long copy(ReadableByteChannel from, WritableByteChannel to) throws IOException {
    Preconditions.checkNotNull(from);
    Preconditions.checkNotNull(to);
    if (from instanceof FileChannel) {
      FileChannel sourceChannel = (FileChannel)from;
      long oldPosition = sourceChannel.position();
      long position = oldPosition;
      while (true) {
        long copied = sourceChannel.transferTo(position, 524288L, to);
        position += copied;
        sourceChannel.position(position);
        if (copied <= 0L && position >= sourceChannel.size())
          return position - oldPosition; 
      } 
    } 
    ByteBuffer buf = ByteBuffer.wrap(createBuffer());
    long total = 0L;
    while (from.read(buf) != -1) {
      Java8Compatibility.flip(buf);
      while (buf.hasRemaining())
        total += to.write(buf); 
      Java8Compatibility.clear(buf);
    } 
    return total;
  }
  
  private static byte[] toByteArrayInternal(InputStream in, Queue<byte[]> bufs, int totalLen) throws IOException {
    int initialBufferSize = Math.min(8192, Math.max(128, Integer.highestOneBit(totalLen) * 2));
    int bufSize = initialBufferSize;
    for (; totalLen < 2147483639; 
      bufSize = IntMath.saturatedMultiply(bufSize, (bufSize < 4096) ? 4 : 2)) {
      byte[] buf = new byte[Math.min(bufSize, 2147483639 - totalLen)];
      bufs.add(buf);
      int off = 0;
      while (off < buf.length) {
        int r = in.read(buf, off, buf.length - off);
        if (r == -1)
          return combineBuffers(bufs, totalLen); 
        off += r;
        totalLen += r;
      } 
    } 
    if (in.read() == -1)
      return combineBuffers(bufs, 2147483639); 
    throw new OutOfMemoryError("input is too large to fit in a byte array");
  }
  
  private static byte[] combineBuffers(Queue<byte[]> bufs, int totalLen) {
    if (bufs.isEmpty())
      return new byte[0]; 
    byte[] result = bufs.remove();
    if (result.length == totalLen)
      return result; 
    int remaining = totalLen - result.length;
    result = Arrays.copyOf(result, totalLen);
    while (remaining > 0) {
      byte[] buf = bufs.remove();
      int bytesToCopy = Math.min(remaining, buf.length);
      int resultOffset = totalLen - remaining;
      System.arraycopy(buf, 0, result, resultOffset, bytesToCopy);
      remaining -= bytesToCopy;
    } 
    return result;
  }
  
  public static byte[] toByteArray(InputStream in) throws IOException {
    Preconditions.checkNotNull(in);
    return toByteArrayInternal(in, (Queue)new ArrayDeque<>(20), 0);
  }
  
  static byte[] toByteArray(InputStream in, long expectedSize) throws IOException {
    Preconditions.checkArgument((expectedSize >= 0L), "expectedSize (%s) must be non-negative", expectedSize);
    if (expectedSize > 2147483639L)
      throw new OutOfMemoryError(expectedSize + " bytes is too large to fit in a byte array"); 
    byte[] bytes = new byte[(int)expectedSize];
    int remaining = (int)expectedSize;
    while (remaining > 0) {
      int off = (int)expectedSize - remaining;
      int read = in.read(bytes, off, remaining);
      if (read == -1)
        return Arrays.copyOf(bytes, off); 
      remaining -= read;
    } 
    int b = in.read();
    if (b == -1)
      return bytes; 
    Queue<byte[]> bufs = (Queue)new ArrayDeque<>(22);
    bufs.add(bytes);
    bufs.add(new byte[] { (byte)b });
    return toByteArrayInternal(in, bufs, bytes.length + 1);
  }
  
  @CanIgnoreReturnValue
  public static long exhaust(InputStream in) throws IOException {
    long total = 0L;
    byte[] buf = createBuffer();
    long read;
    while ((read = in.read(buf)) != -1L)
      total += read; 
    return total;
  }
  
  public static ByteArrayDataInput newDataInput(byte[] bytes) {
    return newDataInput(new ByteArrayInputStream(bytes));
  }
  
  public static ByteArrayDataInput newDataInput(byte[] bytes, int start) {
    Preconditions.checkPositionIndex(start, bytes.length);
    return newDataInput(new ByteArrayInputStream(bytes, start, bytes.length - start));
  }
  
  public static ByteArrayDataInput newDataInput(ByteArrayInputStream byteArrayInputStream) {
    return new ByteArrayDataInputStream((ByteArrayInputStream)Preconditions.checkNotNull(byteArrayInputStream));
  }
  
  private static class ByteArrayDataInputStream implements ByteArrayDataInput {
    final DataInput input;
    
    ByteArrayDataInputStream(ByteArrayInputStream byteArrayInputStream) {
      this.input = new DataInputStream(byteArrayInputStream);
    }
    
    public void readFully(byte[] b) {
      try {
        this.input.readFully(b);
      } catch (IOException e) {
        throw new IllegalStateException(e);
      } 
    }
    
    public void readFully(byte[] b, int off, int len) {
      try {
        this.input.readFully(b, off, len);
      } catch (IOException e) {
        throw new IllegalStateException(e);
      } 
    }
    
    public int skipBytes(int n) {
      try {
        return this.input.skipBytes(n);
      } catch (IOException e) {
        throw new IllegalStateException(e);
      } 
    }
    
    public boolean readBoolean() {
      try {
        return this.input.readBoolean();
      } catch (IOException e) {
        throw new IllegalStateException(e);
      } 
    }
    
    public byte readByte() {
      try {
        return this.input.readByte();
      } catch (EOFException e) {
        throw new IllegalStateException(e);
      } catch (IOException impossible) {
        throw new AssertionError(impossible);
      } 
    }
    
    public int readUnsignedByte() {
      try {
        return this.input.readUnsignedByte();
      } catch (IOException e) {
        throw new IllegalStateException(e);
      } 
    }
    
    public short readShort() {
      try {
        return this.input.readShort();
      } catch (IOException e) {
        throw new IllegalStateException(e);
      } 
    }
    
    public int readUnsignedShort() {
      try {
        return this.input.readUnsignedShort();
      } catch (IOException e) {
        throw new IllegalStateException(e);
      } 
    }
    
    public char readChar() {
      try {
        return this.input.readChar();
      } catch (IOException e) {
        throw new IllegalStateException(e);
      } 
    }
    
    public int readInt() {
      try {
        return this.input.readInt();
      } catch (IOException e) {
        throw new IllegalStateException(e);
      } 
    }
    
    public long readLong() {
      try {
        return this.input.readLong();
      } catch (IOException e) {
        throw new IllegalStateException(e);
      } 
    }
    
    public float readFloat() {
      try {
        return this.input.readFloat();
      } catch (IOException e) {
        throw new IllegalStateException(e);
      } 
    }
    
    public double readDouble() {
      try {
        return this.input.readDouble();
      } catch (IOException e) {
        throw new IllegalStateException(e);
      } 
    }
    
    @CheckForNull
    public String readLine() {
      try {
        return this.input.readLine();
      } catch (IOException e) {
        throw new IllegalStateException(e);
      } 
    }
    
    public String readUTF() {
      try {
        return this.input.readUTF();
      } catch (IOException e) {
        throw new IllegalStateException(e);
      } 
    }
  }
  
  public static ByteArrayDataOutput newDataOutput() {
    return newDataOutput(new ByteArrayOutputStream());
  }
  
  public static ByteArrayDataOutput newDataOutput(int size) {
    if (size < 0)
      throw new IllegalArgumentException(String.format("Invalid size: %s", new Object[] { Integer.valueOf(size) })); 
    return newDataOutput(new ByteArrayOutputStream(size));
  }
  
  public static ByteArrayDataOutput newDataOutput(ByteArrayOutputStream byteArrayOutputStream) {
    return new ByteArrayDataOutputStream((ByteArrayOutputStream)Preconditions.checkNotNull(byteArrayOutputStream));
  }
  
  private static class ByteArrayDataOutputStream implements ByteArrayDataOutput {
    final DataOutput output;
    
    final ByteArrayOutputStream byteArrayOutputStream;
    
    ByteArrayDataOutputStream(ByteArrayOutputStream byteArrayOutputStream) {
      this.byteArrayOutputStream = byteArrayOutputStream;
      this.output = new DataOutputStream(byteArrayOutputStream);
    }
    
    public void write(int b) {
      try {
        this.output.write(b);
      } catch (IOException impossible) {
        throw new AssertionError(impossible);
      } 
    }
    
    public void write(byte[] b) {
      try {
        this.output.write(b);
      } catch (IOException impossible) {
        throw new AssertionError(impossible);
      } 
    }
    
    public void write(byte[] b, int off, int len) {
      try {
        this.output.write(b, off, len);
      } catch (IOException impossible) {
        throw new AssertionError(impossible);
      } 
    }
    
    public void writeBoolean(boolean v) {
      try {
        this.output.writeBoolean(v);
      } catch (IOException impossible) {
        throw new AssertionError(impossible);
      } 
    }
    
    public void writeByte(int v) {
      try {
        this.output.writeByte(v);
      } catch (IOException impossible) {
        throw new AssertionError(impossible);
      } 
    }
    
    public void writeBytes(String s) {
      try {
        this.output.writeBytes(s);
      } catch (IOException impossible) {
        throw new AssertionError(impossible);
      } 
    }
    
    public void writeChar(int v) {
      try {
        this.output.writeChar(v);
      } catch (IOException impossible) {
        throw new AssertionError(impossible);
      } 
    }
    
    public void writeChars(String s) {
      try {
        this.output.writeChars(s);
      } catch (IOException impossible) {
        throw new AssertionError(impossible);
      } 
    }
    
    public void writeDouble(double v) {
      try {
        this.output.writeDouble(v);
      } catch (IOException impossible) {
        throw new AssertionError(impossible);
      } 
    }
    
    public void writeFloat(float v) {
      try {
        this.output.writeFloat(v);
      } catch (IOException impossible) {
        throw new AssertionError(impossible);
      } 
    }
    
    public void writeInt(int v) {
      try {
        this.output.writeInt(v);
      } catch (IOException impossible) {
        throw new AssertionError(impossible);
      } 
    }
    
    public void writeLong(long v) {
      try {
        this.output.writeLong(v);
      } catch (IOException impossible) {
        throw new AssertionError(impossible);
      } 
    }
    
    public void writeShort(int v) {
      try {
        this.output.writeShort(v);
      } catch (IOException impossible) {
        throw new AssertionError(impossible);
      } 
    }
    
    public void writeUTF(String s) {
      try {
        this.output.writeUTF(s);
      } catch (IOException impossible) {
        throw new AssertionError(impossible);
      } 
    }
    
    public byte[] toByteArray() {
      return this.byteArrayOutputStream.toByteArray();
    }
  }
  
  private static final OutputStream NULL_OUTPUT_STREAM = new OutputStream() {
      public void write(int b) {}
      
      public void write(byte[] b) {
        Preconditions.checkNotNull(b);
      }
      
      public void write(byte[] b, int off, int len) {
        Preconditions.checkNotNull(b);
        Preconditions.checkPositionIndexes(off, off + len, b.length);
      }
      
      public String toString() {
        return "ByteStreams.nullOutputStream()";
      }
    };
  
  public static OutputStream nullOutputStream() {
    return NULL_OUTPUT_STREAM;
  }
  
  public static InputStream limit(InputStream in, long limit) {
    return new LimitedInputStream(in, limit);
  }
  
  private static final class LimitedInputStream extends FilterInputStream {
    private long left;
    
    private long mark = -1L;
    
    LimitedInputStream(InputStream in, long limit) {
      super(in);
      Preconditions.checkNotNull(in);
      Preconditions.checkArgument((limit >= 0L), "limit must be non-negative");
      this.left = limit;
    }
    
    public int available() throws IOException {
      return (int)Math.min(this.in.available(), this.left);
    }
    
    public synchronized void mark(int readLimit) {
      this.in.mark(readLimit);
      this.mark = this.left;
    }
    
    public int read() throws IOException {
      if (this.left == 0L)
        return -1; 
      int result = this.in.read();
      if (result != -1)
        this.left--; 
      return result;
    }
    
    public int read(byte[] b, int off, int len) throws IOException {
      if (this.left == 0L)
        return -1; 
      len = (int)Math.min(len, this.left);
      int result = this.in.read(b, off, len);
      if (result != -1)
        this.left -= result; 
      return result;
    }
    
    public synchronized void reset() throws IOException {
      if (!this.in.markSupported())
        throw new IOException("Mark not supported"); 
      if (this.mark == -1L)
        throw new IOException("Mark not set"); 
      this.in.reset();
      this.left = this.mark;
    }
    
    public long skip(long n) throws IOException {
      n = Math.min(n, this.left);
      long skipped = this.in.skip(n);
      this.left -= skipped;
      return skipped;
    }
  }
  
  public static void readFully(InputStream in, byte[] b) throws IOException {
    readFully(in, b, 0, b.length);
  }
  
  public static void readFully(InputStream in, byte[] b, int off, int len) throws IOException {
    int read = read(in, b, off, len);
    if (read != len)
      throw new EOFException("reached end of stream after reading " + read + " bytes; " + len + " bytes expected"); 
  }
  
  public static void skipFully(InputStream in, long n) throws IOException {
    long skipped = skipUpTo(in, n);
    if (skipped < n)
      throw new EOFException("reached end of stream after skipping " + skipped + " bytes; " + n + " bytes expected"); 
  }
  
  static long skipUpTo(InputStream in, long n) throws IOException {
    long totalSkipped = 0L;
    byte[] buf = null;
    while (totalSkipped < n) {
      long remaining = n - totalSkipped;
      long skipped = skipSafely(in, remaining);
      if (skipped == 0L) {
        int skip = (int)Math.min(remaining, 8192L);
        if (buf == null)
          buf = new byte[skip]; 
        if ((skipped = in.read(buf, 0, skip)) == -1L)
          break; 
      } 
      totalSkipped += skipped;
    } 
    return totalSkipped;
  }
  
  private static long skipSafely(InputStream in, long n) throws IOException {
    int available = in.available();
    return (available == 0) ? 0L : in.skip(Math.min(available, n));
  }
  
  @ParametricNullness
  @CanIgnoreReturnValue
  public static <T> T readBytes(InputStream input, ByteProcessor<T> processor) throws IOException {
    int read;
    Preconditions.checkNotNull(input);
    Preconditions.checkNotNull(processor);
    byte[] buf = createBuffer();
    do {
      read = input.read(buf);
    } while (read != -1 && processor.processBytes(buf, 0, read));
    return processor.getResult();
  }
  
  @CanIgnoreReturnValue
  public static int read(InputStream in, byte[] b, int off, int len) throws IOException {
    Preconditions.checkNotNull(in);
    Preconditions.checkNotNull(b);
    if (len < 0)
      throw new IndexOutOfBoundsException(String.format("len (%s) cannot be negative", new Object[] { Integer.valueOf(len) })); 
    Preconditions.checkPositionIndexes(off, off + len, b.length);
    int total = 0;
    while (total < len) {
      int result = in.read(b, off + total, len - total);
      if (result == -1)
        break; 
      total += result;
    } 
    return total;
  }
}
