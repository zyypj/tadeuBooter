package me.syncwrld.booter.libs.google.guava.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.stream.Stream;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;

@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
public abstract class CharSink {
  public abstract Writer openStream() throws IOException;
  
  public Writer openBufferedStream() throws IOException {
    Writer writer = openStream();
    return (writer instanceof BufferedWriter) ? 
      writer : 
      new BufferedWriter(writer);
  }
  
  public void write(CharSequence charSequence) throws IOException {
    Preconditions.checkNotNull(charSequence);
    Closer closer = Closer.create();
    try {
      Writer out = closer.<Writer>register(openStream());
      out.append(charSequence);
      out.flush();
    } catch (Throwable e) {
      throw closer.rethrow(e);
    } finally {
      closer.close();
    } 
  }
  
  public void writeLines(Iterable<? extends CharSequence> lines) throws IOException {
    writeLines(lines, System.getProperty("line.separator"));
  }
  
  public void writeLines(Iterable<? extends CharSequence> lines, String lineSeparator) throws IOException {
    writeLines(lines.iterator(), lineSeparator);
  }
  
  public void writeLines(Stream<? extends CharSequence> lines) throws IOException {
    writeLines(lines, System.getProperty("line.separator"));
  }
  
  public void writeLines(Stream<? extends CharSequence> lines, String lineSeparator) throws IOException {
    writeLines(lines.iterator(), lineSeparator);
  }
  
  private void writeLines(Iterator<? extends CharSequence> lines, String lineSeparator) throws IOException {
    Preconditions.checkNotNull(lineSeparator);
    Writer out = openBufferedStream();
    try {
      while (lines.hasNext())
        out.append(lines.next()).append(lineSeparator); 
      if (out != null)
        out.close(); 
    } catch (Throwable throwable) {
      if (out != null)
        try {
          out.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        }  
      throw throwable;
    } 
  }
  
  @CanIgnoreReturnValue
  public long writeFrom(Readable readable) throws IOException {
    Preconditions.checkNotNull(readable);
    Closer closer = Closer.create();
    try {
      Writer out = closer.<Writer>register(openStream());
      long written = CharStreams.copy(readable, out);
      out.flush();
      return written;
    } catch (Throwable e) {
      throw closer.rethrow(e);
    } finally {
      closer.close();
    } 
  }
}
