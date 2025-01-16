package me.syncwrld.booter.libs.google.gson.internal;

import java.io.EOFException;
import java.io.IOException;
import java.io.Writer;
import java.util.Objects;
import me.syncwrld.booter.libs.google.gson.JsonElement;
import me.syncwrld.booter.libs.google.gson.JsonIOException;
import me.syncwrld.booter.libs.google.gson.JsonNull;
import me.syncwrld.booter.libs.google.gson.JsonParseException;
import me.syncwrld.booter.libs.google.gson.JsonSyntaxException;
import me.syncwrld.booter.libs.google.gson.internal.bind.TypeAdapters;
import me.syncwrld.booter.libs.google.gson.stream.JsonReader;
import me.syncwrld.booter.libs.google.gson.stream.JsonWriter;
import me.syncwrld.booter.libs.google.gson.stream.MalformedJsonException;

public final class Streams {
  private Streams() {
    throw new UnsupportedOperationException();
  }
  
  public static JsonElement parse(JsonReader reader) throws JsonParseException {
    boolean isEmpty = true;
    try {
      reader.peek();
      isEmpty = false;
      return (JsonElement)TypeAdapters.JSON_ELEMENT.read(reader);
    } catch (EOFException e) {
      if (isEmpty)
        return (JsonElement)JsonNull.INSTANCE; 
      throw new JsonSyntaxException(e);
    } catch (MalformedJsonException e) {
      throw new JsonSyntaxException(e);
    } catch (IOException e) {
      throw new JsonIOException(e);
    } catch (NumberFormatException e) {
      throw new JsonSyntaxException(e);
    } 
  }
  
  public static void write(JsonElement element, JsonWriter writer) throws IOException {
    TypeAdapters.JSON_ELEMENT.write(writer, element);
  }
  
  public static Writer writerForAppendable(Appendable appendable) {
    return (appendable instanceof Writer) ? (Writer)appendable : new AppendableWriter(appendable);
  }
  
  private static final class AppendableWriter extends Writer {
    private final Appendable appendable;
    
    private final CurrentWrite currentWrite = new CurrentWrite();
    
    AppendableWriter(Appendable appendable) {
      this.appendable = appendable;
    }
    
    public void write(char[] chars, int offset, int length) throws IOException {
      this.currentWrite.setChars(chars);
      this.appendable.append(this.currentWrite, offset, offset + length);
    }
    
    public void flush() {}
    
    public void close() {}
    
    public void write(int i) throws IOException {
      this.appendable.append((char)i);
    }
    
    public void write(String str, int off, int len) throws IOException {
      Objects.requireNonNull(str);
      this.appendable.append(str, off, off + len);
    }
    
    public Writer append(CharSequence csq) throws IOException {
      this.appendable.append(csq);
      return this;
    }
    
    public Writer append(CharSequence csq, int start, int end) throws IOException {
      this.appendable.append(csq, start, end);
      return this;
    }
    
    private static class CurrentWrite implements CharSequence {
      private char[] chars;
      
      private String cachedString;
      
      private CurrentWrite() {}
      
      void setChars(char[] chars) {
        this.chars = chars;
        this.cachedString = null;
      }
      
      public int length() {
        return this.chars.length;
      }
      
      public char charAt(int i) {
        return this.chars[i];
      }
      
      public CharSequence subSequence(int start, int end) {
        return new String(this.chars, start, end - start);
      }
      
      public String toString() {
        if (this.cachedString == null)
          this.cachedString = new String(this.chars); 
        return this.cachedString;
      }
    }
  }
}
