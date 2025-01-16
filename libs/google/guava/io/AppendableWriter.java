package me.syncwrld.booter.libs.google.guava.io;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
class AppendableWriter extends Writer {
  private final Appendable target;
  
  private boolean closed;
  
  AppendableWriter(Appendable target) {
    this.target = (Appendable)Preconditions.checkNotNull(target);
  }
  
  public void write(char[] cbuf, int off, int len) throws IOException {
    checkNotClosed();
    this.target.append(new String(cbuf, off, len));
  }
  
  public void write(int c) throws IOException {
    checkNotClosed();
    this.target.append((char)c);
  }
  
  public void write(String str) throws IOException {
    Preconditions.checkNotNull(str);
    checkNotClosed();
    this.target.append(str);
  }
  
  public void write(String str, int off, int len) throws IOException {
    Preconditions.checkNotNull(str);
    checkNotClosed();
    this.target.append(str, off, off + len);
  }
  
  public void flush() throws IOException {
    checkNotClosed();
    if (this.target instanceof Flushable)
      ((Flushable)this.target).flush(); 
  }
  
  public void close() throws IOException {
    this.closed = true;
    if (this.target instanceof Closeable)
      ((Closeable)this.target).close(); 
  }
  
  public Writer append(char c) throws IOException {
    checkNotClosed();
    this.target.append(c);
    return this;
  }
  
  public Writer append(@CheckForNull CharSequence charSeq) throws IOException {
    checkNotClosed();
    this.target.append(charSeq);
    return this;
  }
  
  public Writer append(@CheckForNull CharSequence charSeq, int start, int end) throws IOException {
    checkNotClosed();
    this.target.append(charSeq, start, end);
    return this;
  }
  
  private void checkNotClosed() throws IOException {
    if (this.closed)
      throw new IOException("Cannot write to a closed writer."); 
  }
}
