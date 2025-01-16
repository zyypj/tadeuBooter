package me.syncwrld.booter.libs.google.guava.io;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.ArrayDeque;
import java.util.Queue;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
public final class LineReader {
  private final Readable readable;
  
  @CheckForNull
  private final Reader reader;
  
  private final CharBuffer cbuf = CharStreams.createBuffer();
  
  private final char[] buf = this.cbuf.array();
  
  private final Queue<String> lines = new ArrayDeque<>();
  
  private final LineBuffer lineBuf = new LineBuffer() {
      protected void handleLine(String line, String end) {
        LineReader.this.lines.add(line);
      }
    };
  
  public LineReader(Readable readable) {
    this.readable = (Readable)Preconditions.checkNotNull(readable);
    this.reader = (readable instanceof Reader) ? (Reader)readable : null;
  }
  
  @CheckForNull
  @CanIgnoreReturnValue
  public String readLine() throws IOException {
    while (this.lines.peek() == null) {
      Java8Compatibility.clear(this.cbuf);
      int read = (this.reader != null) ? this.reader.read(this.buf, 0, this.buf.length) : this.readable.read(this.cbuf);
      if (read == -1) {
        this.lineBuf.finish();
        break;
      } 
      this.lineBuf.add(this.buf, 0, read);
    } 
    return this.lines.poll();
  }
}
