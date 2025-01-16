package me.syncwrld.booter.libs.google.guava.io;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.Objects;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
final class CharSequenceReader extends Reader {
  @CheckForNull
  private CharSequence seq;
  
  private int pos;
  
  private int mark;
  
  public CharSequenceReader(CharSequence seq) {
    this.seq = (CharSequence)Preconditions.checkNotNull(seq);
  }
  
  private void checkOpen() throws IOException {
    if (this.seq == null)
      throw new IOException("reader closed"); 
  }
  
  private boolean hasRemaining() {
    return (remaining() > 0);
  }
  
  private int remaining() {
    Objects.requireNonNull(this.seq);
    return this.seq.length() - this.pos;
  }
  
  public synchronized int read(CharBuffer target) throws IOException {
    Preconditions.checkNotNull(target);
    checkOpen();
    Objects.requireNonNull(this.seq);
    if (!hasRemaining())
      return -1; 
    int charsToRead = Math.min(target.remaining(), remaining());
    for (int i = 0; i < charsToRead; i++)
      target.put(this.seq.charAt(this.pos++)); 
    return charsToRead;
  }
  
  public synchronized int read() throws IOException {
    checkOpen();
    Objects.requireNonNull(this.seq);
    return hasRemaining() ? this.seq.charAt(this.pos++) : -1;
  }
  
  public synchronized int read(char[] cbuf, int off, int len) throws IOException {
    Preconditions.checkPositionIndexes(off, off + len, cbuf.length);
    checkOpen();
    Objects.requireNonNull(this.seq);
    if (!hasRemaining())
      return -1; 
    int charsToRead = Math.min(len, remaining());
    for (int i = 0; i < charsToRead; i++)
      cbuf[off + i] = this.seq.charAt(this.pos++); 
    return charsToRead;
  }
  
  public synchronized long skip(long n) throws IOException {
    Preconditions.checkArgument((n >= 0L), "n (%s) may not be negative", n);
    checkOpen();
    int charsToSkip = (int)Math.min(remaining(), n);
    this.pos += charsToSkip;
    return charsToSkip;
  }
  
  public synchronized boolean ready() throws IOException {
    checkOpen();
    return true;
  }
  
  public boolean markSupported() {
    return true;
  }
  
  public synchronized void mark(int readAheadLimit) throws IOException {
    Preconditions.checkArgument((readAheadLimit >= 0), "readAheadLimit (%s) may not be negative", readAheadLimit);
    checkOpen();
    this.mark = this.pos;
  }
  
  public synchronized void reset() throws IOException {
    checkOpen();
    this.pos = this.mark;
  }
  
  public synchronized void close() throws IOException {
    this.seq = null;
  }
}
