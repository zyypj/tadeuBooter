package me.syncwrld.booter.libs.google.guava.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Objects;
import me.syncwrld.booter.libs.google.errorprone.annotations.concurrent.LazyInit;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.base.Ascii;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.guava.math.IntMath;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible(emulated = true)
public abstract class BaseEncoding {
  public static final class DecodingException extends IOException {
    DecodingException(String message) {
      super(message);
    }
    
    DecodingException(Throwable cause) {
      super(cause);
    }
  }
  
  public String encode(byte[] bytes) {
    return encode(bytes, 0, bytes.length);
  }
  
  public final String encode(byte[] bytes, int off, int len) {
    Preconditions.checkPositionIndexes(off, off + len, bytes.length);
    StringBuilder result = new StringBuilder(maxEncodedSize(len));
    try {
      encodeTo(result, bytes, off, len);
    } catch (IOException impossible) {
      throw new AssertionError(impossible);
    } 
    return result.toString();
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  public abstract OutputStream encodingStream(Writer paramWriter);
  
  @J2ktIncompatible
  @GwtIncompatible
  public final ByteSink encodingSink(final CharSink encodedSink) {
    Preconditions.checkNotNull(encodedSink);
    return new ByteSink() {
        public OutputStream openStream() throws IOException {
          return BaseEncoding.this.encodingStream(encodedSink.openStream());
        }
      };
  }
  
  private static byte[] extract(byte[] result, int length) {
    if (length == result.length)
      return result; 
    byte[] trunc = new byte[length];
    System.arraycopy(result, 0, trunc, 0, length);
    return trunc;
  }
  
  public abstract boolean canDecode(CharSequence paramCharSequence);
  
  public final byte[] decode(CharSequence chars) {
    try {
      return decodeChecked(chars);
    } catch (DecodingException badInput) {
      throw new IllegalArgumentException(badInput);
    } 
  }
  
  final byte[] decodeChecked(CharSequence chars) throws DecodingException {
    chars = trimTrailingPadding(chars);
    byte[] tmp = new byte[maxDecodedSize(chars.length())];
    int len = decodeTo(tmp, chars);
    return extract(tmp, len);
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  public abstract InputStream decodingStream(Reader paramReader);
  
  @J2ktIncompatible
  @GwtIncompatible
  public final ByteSource decodingSource(final CharSource encodedSource) {
    Preconditions.checkNotNull(encodedSource);
    return new ByteSource() {
        public InputStream openStream() throws IOException {
          return BaseEncoding.this.decodingStream(encodedSource.openStream());
        }
      };
  }
  
  abstract int maxEncodedSize(int paramInt);
  
  abstract void encodeTo(Appendable paramAppendable, byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException;
  
  abstract int maxDecodedSize(int paramInt);
  
  abstract int decodeTo(byte[] paramArrayOfbyte, CharSequence paramCharSequence) throws DecodingException;
  
  CharSequence trimTrailingPadding(CharSequence chars) {
    return (CharSequence)Preconditions.checkNotNull(chars);
  }
  
  private static final BaseEncoding BASE64 = new Base64Encoding("base64()", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/", 
      
      Character.valueOf('='));
  
  public abstract BaseEncoding omitPadding();
  
  public abstract BaseEncoding withPadChar(char paramChar);
  
  public abstract BaseEncoding withSeparator(String paramString, int paramInt);
  
  public abstract BaseEncoding upperCase();
  
  public abstract BaseEncoding lowerCase();
  
  public abstract BaseEncoding ignoreCase();
  
  public static BaseEncoding base64() {
    return BASE64;
  }
  
  private static final BaseEncoding BASE64_URL = new Base64Encoding("base64Url()", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_", 
      
      Character.valueOf('='));
  
  public static BaseEncoding base64Url() {
    return BASE64_URL;
  }
  
  private static final BaseEncoding BASE32 = new StandardBaseEncoding("base32()", "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567", 
      Character.valueOf('='));
  
  public static BaseEncoding base32() {
    return BASE32;
  }
  
  private static final BaseEncoding BASE32_HEX = new StandardBaseEncoding("base32Hex()", "0123456789ABCDEFGHIJKLMNOPQRSTUV", 
      Character.valueOf('='));
  
  public static BaseEncoding base32Hex() {
    return BASE32_HEX;
  }
  
  private static final BaseEncoding BASE16 = new Base16Encoding("base16()", "0123456789ABCDEF");
  
  public static BaseEncoding base16() {
    return BASE16;
  }
  
  static final class Alphabet {
    private final String name;
    
    private final char[] chars;
    
    final int mask;
    
    final int bitsPerChar;
    
    final int charsPerChunk;
    
    final int bytesPerChunk;
    
    private final byte[] decodabet;
    
    private final boolean[] validPadding;
    
    private final boolean ignoreCase;
    
    Alphabet(String name, char[] chars) {
      this(name, chars, decodabetFor(chars), false);
    }
    
    private Alphabet(String name, char[] chars, byte[] decodabet, boolean ignoreCase) {
      this.name = (String)Preconditions.checkNotNull(name);
      this.chars = (char[])Preconditions.checkNotNull(chars);
      try {
        this.bitsPerChar = IntMath.log2(chars.length, RoundingMode.UNNECESSARY);
      } catch (ArithmeticException e) {
        throw new IllegalArgumentException("Illegal alphabet length " + chars.length, e);
      } 
      int zeroesInBitsPerChar = Integer.numberOfTrailingZeros(this.bitsPerChar);
      this.charsPerChunk = 1 << 3 - zeroesInBitsPerChar;
      this.bytesPerChunk = this.bitsPerChar >> zeroesInBitsPerChar;
      this.mask = chars.length - 1;
      this.decodabet = decodabet;
      boolean[] validPadding = new boolean[this.charsPerChunk];
      for (int i = 0; i < this.bytesPerChunk; i++)
        validPadding[IntMath.divide(i * 8, this.bitsPerChar, RoundingMode.CEILING)] = true; 
      this.validPadding = validPadding;
      this.ignoreCase = ignoreCase;
    }
    
    private static byte[] decodabetFor(char[] chars) {
      byte[] decodabet = new byte[128];
      Arrays.fill(decodabet, (byte)-1);
      for (int i = 0; i < chars.length; i++) {
        char c = chars[i];
        Preconditions.checkArgument((c < decodabet.length), "Non-ASCII character: %s", c);
        Preconditions.checkArgument((decodabet[c] == -1), "Duplicate character: %s", c);
        decodabet[c] = (byte)i;
      } 
      return decodabet;
    }
    
    Alphabet ignoreCase() {
      if (this.ignoreCase)
        return this; 
      byte[] newDecodabet = Arrays.copyOf(this.decodabet, this.decodabet.length);
      for (int upper = 65; upper <= 90; upper++) {
        int lower = upper | 0x20;
        byte decodeUpper = this.decodabet[upper];
        byte decodeLower = this.decodabet[lower];
        if (decodeUpper == -1) {
          newDecodabet[upper] = decodeLower;
        } else {
          Preconditions.checkState((decodeLower == -1), "Can't ignoreCase() since '%s' and '%s' encode different values", (char)upper, (char)lower);
          newDecodabet[lower] = decodeUpper;
        } 
      } 
      return new Alphabet(this.name + ".ignoreCase()", this.chars, newDecodabet, true);
    }
    
    char encode(int bits) {
      return this.chars[bits];
    }
    
    boolean isValidPaddingStartPosition(int index) {
      return this.validPadding[index % this.charsPerChunk];
    }
    
    boolean canDecode(char ch) {
      return (ch <= '' && this.decodabet[ch] != -1);
    }
    
    int decode(char ch) throws BaseEncoding.DecodingException {
      if (ch > '')
        throw new BaseEncoding.DecodingException("Unrecognized character: 0x" + Integer.toHexString(ch)); 
      int result = this.decodabet[ch];
      if (result == -1) {
        if (ch <= ' ' || ch == '')
          throw new BaseEncoding.DecodingException("Unrecognized character: 0x" + Integer.toHexString(ch)); 
        throw new BaseEncoding.DecodingException("Unrecognized character: " + ch);
      } 
      return result;
    }
    
    private boolean hasLowerCase() {
      for (char c : this.chars) {
        if (Ascii.isLowerCase(c))
          return true; 
      } 
      return false;
    }
    
    private boolean hasUpperCase() {
      for (char c : this.chars) {
        if (Ascii.isUpperCase(c))
          return true; 
      } 
      return false;
    }
    
    Alphabet upperCase() {
      if (!hasLowerCase())
        return this; 
      Preconditions.checkState(!hasUpperCase(), "Cannot call upperCase() on a mixed-case alphabet");
      char[] upperCased = new char[this.chars.length];
      for (int i = 0; i < this.chars.length; i++)
        upperCased[i] = Ascii.toUpperCase(this.chars[i]); 
      Alphabet upperCase = new Alphabet(this.name + ".upperCase()", upperCased);
      return this.ignoreCase ? upperCase.ignoreCase() : upperCase;
    }
    
    Alphabet lowerCase() {
      if (!hasUpperCase())
        return this; 
      Preconditions.checkState(!hasLowerCase(), "Cannot call lowerCase() on a mixed-case alphabet");
      char[] lowerCased = new char[this.chars.length];
      for (int i = 0; i < this.chars.length; i++)
        lowerCased[i] = Ascii.toLowerCase(this.chars[i]); 
      Alphabet lowerCase = new Alphabet(this.name + ".lowerCase()", lowerCased);
      return this.ignoreCase ? lowerCase.ignoreCase() : lowerCase;
    }
    
    public boolean matches(char c) {
      return (c < this.decodabet.length && this.decodabet[c] != -1);
    }
    
    public String toString() {
      return this.name;
    }
    
    public boolean equals(@CheckForNull Object other) {
      if (other instanceof Alphabet) {
        Alphabet that = (Alphabet)other;
        return (this.ignoreCase == that.ignoreCase && Arrays.equals(this.chars, that.chars));
      } 
      return false;
    }
    
    public int hashCode() {
      return Arrays.hashCode(this.chars) + (this.ignoreCase ? 1231 : 1237);
    }
  }
  
  static class StandardBaseEncoding extends BaseEncoding {
    final BaseEncoding.Alphabet alphabet;
    
    @CheckForNull
    final Character paddingChar;
    
    @LazyInit
    @CheckForNull
    private volatile BaseEncoding upperCase;
    
    @LazyInit
    @CheckForNull
    private volatile BaseEncoding lowerCase;
    
    @LazyInit
    @CheckForNull
    private volatile BaseEncoding ignoreCase;
    
    StandardBaseEncoding(String name, String alphabetChars, @CheckForNull Character paddingChar) {
      this(new BaseEncoding.Alphabet(name, alphabetChars.toCharArray()), paddingChar);
    }
    
    StandardBaseEncoding(BaseEncoding.Alphabet alphabet, @CheckForNull Character paddingChar) {
      this.alphabet = (BaseEncoding.Alphabet)Preconditions.checkNotNull(alphabet);
      Preconditions.checkArgument((paddingChar == null || 
          !alphabet.matches(paddingChar.charValue())), "Padding character %s was already in alphabet", paddingChar);
      this.paddingChar = paddingChar;
    }
    
    int maxEncodedSize(int bytes) {
      return this.alphabet.charsPerChunk * IntMath.divide(bytes, this.alphabet.bytesPerChunk, RoundingMode.CEILING);
    }
    
    @J2ktIncompatible
    @GwtIncompatible
    public OutputStream encodingStream(final Writer out) {
      Preconditions.checkNotNull(out);
      return new OutputStream() {
          int bitBuffer = 0;
          
          int bitBufferLength = 0;
          
          int writtenChars = 0;
          
          public void write(int b) throws IOException {
            this.bitBuffer <<= 8;
            this.bitBuffer |= b & 0xFF;
            this.bitBufferLength += 8;
            while (this.bitBufferLength >= BaseEncoding.StandardBaseEncoding.this.alphabet.bitsPerChar) {
              int charIndex = this.bitBuffer >> this.bitBufferLength - BaseEncoding.StandardBaseEncoding.this.alphabet.bitsPerChar & BaseEncoding.StandardBaseEncoding.this.alphabet.mask;
              out.write(BaseEncoding.StandardBaseEncoding.this.alphabet.encode(charIndex));
              this.writtenChars++;
              this.bitBufferLength -= BaseEncoding.StandardBaseEncoding.this.alphabet.bitsPerChar;
            } 
          }
          
          public void flush() throws IOException {
            out.flush();
          }
          
          public void close() throws IOException {
            if (this.bitBufferLength > 0) {
              int charIndex = this.bitBuffer << BaseEncoding.StandardBaseEncoding.this.alphabet.bitsPerChar - this.bitBufferLength & BaseEncoding.StandardBaseEncoding.this.alphabet.mask;
              out.write(BaseEncoding.StandardBaseEncoding.this.alphabet.encode(charIndex));
              this.writtenChars++;
              if (BaseEncoding.StandardBaseEncoding.this.paddingChar != null)
                while (this.writtenChars % BaseEncoding.StandardBaseEncoding.this.alphabet.charsPerChunk != 0) {
                  out.write(BaseEncoding.StandardBaseEncoding.this.paddingChar.charValue());
                  this.writtenChars++;
                }  
            } 
            out.close();
          }
        };
    }
    
    void encodeTo(Appendable target, byte[] bytes, int off, int len) throws IOException {
      Preconditions.checkNotNull(target);
      Preconditions.checkPositionIndexes(off, off + len, bytes.length);
      int i;
      for (i = 0; i < len; i += this.alphabet.bytesPerChunk)
        encodeChunkTo(target, bytes, off + i, Math.min(this.alphabet.bytesPerChunk, len - i)); 
    }
    
    void encodeChunkTo(Appendable target, byte[] bytes, int off, int len) throws IOException {
      Preconditions.checkNotNull(target);
      Preconditions.checkPositionIndexes(off, off + len, bytes.length);
      Preconditions.checkArgument((len <= this.alphabet.bytesPerChunk));
      long bitBuffer = 0L;
      for (int i = 0; i < len; i++) {
        bitBuffer |= (bytes[off + i] & 0xFF);
        bitBuffer <<= 8L;
      } 
      int bitOffset = (len + 1) * 8 - this.alphabet.bitsPerChar;
      int bitsProcessed = 0;
      while (bitsProcessed < len * 8) {
        int charIndex = (int)(bitBuffer >>> bitOffset - bitsProcessed) & this.alphabet.mask;
        target.append(this.alphabet.encode(charIndex));
        bitsProcessed += this.alphabet.bitsPerChar;
      } 
      if (this.paddingChar != null)
        while (bitsProcessed < this.alphabet.bytesPerChunk * 8) {
          target.append(this.paddingChar.charValue());
          bitsProcessed += this.alphabet.bitsPerChar;
        }  
    }
    
    int maxDecodedSize(int chars) {
      return (int)((this.alphabet.bitsPerChar * chars + 7L) / 8L);
    }
    
    CharSequence trimTrailingPadding(CharSequence chars) {
      Preconditions.checkNotNull(chars);
      if (this.paddingChar == null)
        return chars; 
      char padChar = this.paddingChar.charValue();
      int l;
      for (l = chars.length() - 1; l >= 0 && 
        chars.charAt(l) == padChar; l--);
      return chars.subSequence(0, l + 1);
    }
    
    public boolean canDecode(CharSequence chars) {
      Preconditions.checkNotNull(chars);
      chars = trimTrailingPadding(chars);
      if (!this.alphabet.isValidPaddingStartPosition(chars.length()))
        return false; 
      for (int i = 0; i < chars.length(); i++) {
        if (!this.alphabet.canDecode(chars.charAt(i)))
          return false; 
      } 
      return true;
    }
    
    int decodeTo(byte[] target, CharSequence chars) throws BaseEncoding.DecodingException {
      Preconditions.checkNotNull(target);
      chars = trimTrailingPadding(chars);
      if (!this.alphabet.isValidPaddingStartPosition(chars.length()))
        throw new BaseEncoding.DecodingException("Invalid input length " + chars.length()); 
      int bytesWritten = 0;
      int charIdx;
      for (charIdx = 0; charIdx < chars.length(); charIdx += this.alphabet.charsPerChunk) {
        long chunk = 0L;
        int charsProcessed = 0;
        for (int i = 0; i < this.alphabet.charsPerChunk; i++) {
          chunk <<= this.alphabet.bitsPerChar;
          if (charIdx + i < chars.length())
            chunk |= this.alphabet.decode(chars.charAt(charIdx + charsProcessed++)); 
        } 
        int minOffset = this.alphabet.bytesPerChunk * 8 - charsProcessed * this.alphabet.bitsPerChar;
        for (int offset = (this.alphabet.bytesPerChunk - 1) * 8; offset >= minOffset; offset -= 8)
          target[bytesWritten++] = (byte)(int)(chunk >>> offset & 0xFFL); 
      } 
      return bytesWritten;
    }
    
    @J2ktIncompatible
    @GwtIncompatible
    public InputStream decodingStream(final Reader reader) {
      Preconditions.checkNotNull(reader);
      return new InputStream() {
          int bitBuffer = 0;
          
          int bitBufferLength = 0;
          
          int readChars = 0;
          
          boolean hitPadding = false;
          
          public int read() throws IOException {
            while (true) {
              int readChar = reader.read();
              if (readChar == -1) {
                if (!this.hitPadding && !BaseEncoding.StandardBaseEncoding.this.alphabet.isValidPaddingStartPosition(this.readChars))
                  throw new BaseEncoding.DecodingException("Invalid input length " + this.readChars); 
                return -1;
              } 
              this.readChars++;
              char ch = (char)readChar;
              if (BaseEncoding.StandardBaseEncoding.this.paddingChar != null && BaseEncoding.StandardBaseEncoding.this.paddingChar.charValue() == ch) {
                if (!this.hitPadding && (this.readChars == 1 || 
                  !BaseEncoding.StandardBaseEncoding.this.alphabet.isValidPaddingStartPosition(this.readChars - 1)))
                  throw new BaseEncoding.DecodingException("Padding cannot start at index " + this.readChars); 
                this.hitPadding = true;
                continue;
              } 
              if (this.hitPadding)
                throw new BaseEncoding.DecodingException("Expected padding character but found '" + ch + "' at index " + this.readChars); 
              this.bitBuffer <<= BaseEncoding.StandardBaseEncoding.this.alphabet.bitsPerChar;
              this.bitBuffer |= BaseEncoding.StandardBaseEncoding.this.alphabet.decode(ch);
              this.bitBufferLength += BaseEncoding.StandardBaseEncoding.this.alphabet.bitsPerChar;
              if (this.bitBufferLength >= 8) {
                this.bitBufferLength -= 8;
                return this.bitBuffer >> this.bitBufferLength & 0xFF;
              } 
            } 
          }
          
          public int read(byte[] buf, int off, int len) throws IOException {
            Preconditions.checkPositionIndexes(off, off + len, buf.length);
            int i = off;
            for (; i < off + len; i++) {
              int b = read();
              if (b == -1) {
                int read = i - off;
                return (read == 0) ? -1 : read;
              } 
              buf[i] = (byte)b;
            } 
            return i - off;
          }
          
          public void close() throws IOException {
            reader.close();
          }
        };
    }
    
    public BaseEncoding omitPadding() {
      return (this.paddingChar == null) ? this : newInstance(this.alphabet, null);
    }
    
    public BaseEncoding withPadChar(char padChar) {
      if (8 % this.alphabet.bitsPerChar == 0 || (this.paddingChar != null && this.paddingChar
        .charValue() == padChar))
        return this; 
      return newInstance(this.alphabet, Character.valueOf(padChar));
    }
    
    public BaseEncoding withSeparator(String separator, int afterEveryChars) {
      for (int i = 0; i < separator.length(); i++)
        Preconditions.checkArgument(
            !this.alphabet.matches(separator.charAt(i)), "Separator (%s) cannot contain alphabet characters", separator); 
      if (this.paddingChar != null)
        Preconditions.checkArgument(
            (separator.indexOf(this.paddingChar.charValue()) < 0), "Separator (%s) cannot contain padding character", separator); 
      return new BaseEncoding.SeparatedBaseEncoding(this, separator, afterEveryChars);
    }
    
    public BaseEncoding upperCase() {
      BaseEncoding result = this.upperCase;
      if (result == null) {
        BaseEncoding.Alphabet upper = this.alphabet.upperCase();
        result = this.upperCase = (upper == this.alphabet) ? this : newInstance(upper, this.paddingChar);
      } 
      return result;
    }
    
    public BaseEncoding lowerCase() {
      BaseEncoding result = this.lowerCase;
      if (result == null) {
        BaseEncoding.Alphabet lower = this.alphabet.lowerCase();
        result = this.lowerCase = (lower == this.alphabet) ? this : newInstance(lower, this.paddingChar);
      } 
      return result;
    }
    
    public BaseEncoding ignoreCase() {
      BaseEncoding result = this.ignoreCase;
      if (result == null) {
        BaseEncoding.Alphabet ignore = this.alphabet.ignoreCase();
        result = this.ignoreCase = (ignore == this.alphabet) ? this : newInstance(ignore, this.paddingChar);
      } 
      return result;
    }
    
    BaseEncoding newInstance(BaseEncoding.Alphabet alphabet, @CheckForNull Character paddingChar) {
      return new StandardBaseEncoding(alphabet, paddingChar);
    }
    
    public String toString() {
      StringBuilder builder = new StringBuilder("BaseEncoding.");
      builder.append(this.alphabet);
      if (8 % this.alphabet.bitsPerChar != 0)
        if (this.paddingChar == null) {
          builder.append(".omitPadding()");
        } else {
          builder.append(".withPadChar('").append(this.paddingChar).append("')");
        }  
      return builder.toString();
    }
    
    public boolean equals(@CheckForNull Object other) {
      if (other instanceof StandardBaseEncoding) {
        StandardBaseEncoding that = (StandardBaseEncoding)other;
        return (this.alphabet.equals(that.alphabet) && 
          Objects.equals(this.paddingChar, that.paddingChar));
      } 
      return false;
    }
    
    public int hashCode() {
      return this.alphabet.hashCode() ^ Objects.hashCode(this.paddingChar);
    }
  }
  
  static final class Base16Encoding extends StandardBaseEncoding {
    final char[] encoding = new char[512];
    
    Base16Encoding(String name, String alphabetChars) {
      this(new BaseEncoding.Alphabet(name, alphabetChars.toCharArray()));
    }
    
    private Base16Encoding(BaseEncoding.Alphabet alphabet) {
      super(alphabet, null);
      Preconditions.checkArgument((alphabet.chars.length == 16));
      for (int i = 0; i < 256; i++) {
        this.encoding[i] = alphabet.encode(i >>> 4);
        this.encoding[i | 0x100] = alphabet.encode(i & 0xF);
      } 
    }
    
    void encodeTo(Appendable target, byte[] bytes, int off, int len) throws IOException {
      Preconditions.checkNotNull(target);
      Preconditions.checkPositionIndexes(off, off + len, bytes.length);
      for (int i = 0; i < len; i++) {
        int b = bytes[off + i] & 0xFF;
        target.append(this.encoding[b]);
        target.append(this.encoding[b | 0x100]);
      } 
    }
    
    int decodeTo(byte[] target, CharSequence chars) throws BaseEncoding.DecodingException {
      Preconditions.checkNotNull(target);
      if (chars.length() % 2 == 1)
        throw new BaseEncoding.DecodingException("Invalid input length " + chars.length()); 
      int bytesWritten = 0;
      for (int i = 0; i < chars.length(); i += 2) {
        int decoded = this.alphabet.decode(chars.charAt(i)) << 4 | this.alphabet.decode(chars.charAt(i + 1));
        target[bytesWritten++] = (byte)decoded;
      } 
      return bytesWritten;
    }
    
    BaseEncoding newInstance(BaseEncoding.Alphabet alphabet, @CheckForNull Character paddingChar) {
      return new Base16Encoding(alphabet);
    }
  }
  
  static final class Base64Encoding extends StandardBaseEncoding {
    Base64Encoding(String name, String alphabetChars, @CheckForNull Character paddingChar) {
      this(new BaseEncoding.Alphabet(name, alphabetChars.toCharArray()), paddingChar);
    }
    
    private Base64Encoding(BaseEncoding.Alphabet alphabet, @CheckForNull Character paddingChar) {
      super(alphabet, paddingChar);
      Preconditions.checkArgument((alphabet.chars.length == 64));
    }
    
    void encodeTo(Appendable target, byte[] bytes, int off, int len) throws IOException {
      Preconditions.checkNotNull(target);
      Preconditions.checkPositionIndexes(off, off + len, bytes.length);
      int i = off;
      for (int remaining = len; remaining >= 3; remaining -= 3) {
        int chunk = (bytes[i++] & 0xFF) << 16 | (bytes[i++] & 0xFF) << 8 | bytes[i++] & 0xFF;
        target.append(this.alphabet.encode(chunk >>> 18));
        target.append(this.alphabet.encode(chunk >>> 12 & 0x3F));
        target.append(this.alphabet.encode(chunk >>> 6 & 0x3F));
        target.append(this.alphabet.encode(chunk & 0x3F));
      } 
      if (i < off + len)
        encodeChunkTo(target, bytes, i, off + len - i); 
    }
    
    int decodeTo(byte[] target, CharSequence chars) throws BaseEncoding.DecodingException {
      Preconditions.checkNotNull(target);
      chars = trimTrailingPadding(chars);
      if (!this.alphabet.isValidPaddingStartPosition(chars.length()))
        throw new BaseEncoding.DecodingException("Invalid input length " + chars.length()); 
      int bytesWritten = 0;
      for (int i = 0; i < chars.length(); ) {
        int chunk = this.alphabet.decode(chars.charAt(i++)) << 18;
        chunk |= this.alphabet.decode(chars.charAt(i++)) << 12;
        target[bytesWritten++] = (byte)(chunk >>> 16);
        if (i < chars.length()) {
          chunk |= this.alphabet.decode(chars.charAt(i++)) << 6;
          target[bytesWritten++] = (byte)(chunk >>> 8 & 0xFF);
          if (i < chars.length()) {
            chunk |= this.alphabet.decode(chars.charAt(i++));
            target[bytesWritten++] = (byte)(chunk & 0xFF);
          } 
        } 
      } 
      return bytesWritten;
    }
    
    BaseEncoding newInstance(BaseEncoding.Alphabet alphabet, @CheckForNull Character paddingChar) {
      return new Base64Encoding(alphabet, paddingChar);
    }
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  static Reader ignoringReader(final Reader delegate, final String toIgnore) {
    Preconditions.checkNotNull(delegate);
    Preconditions.checkNotNull(toIgnore);
    return new Reader() {
        public int read() throws IOException {
          int readChar;
          do {
            readChar = delegate.read();
          } while (readChar != -1 && toIgnore.indexOf((char)readChar) >= 0);
          return readChar;
        }
        
        public int read(char[] cbuf, int off, int len) throws IOException {
          throw new UnsupportedOperationException();
        }
        
        public void close() throws IOException {
          delegate.close();
        }
      };
  }
  
  static Appendable separatingAppendable(final Appendable delegate, final String separator, final int afterEveryChars) {
    Preconditions.checkNotNull(delegate);
    Preconditions.checkNotNull(separator);
    Preconditions.checkArgument((afterEveryChars > 0));
    return new Appendable() {
        int charsUntilSeparator = afterEveryChars;
        
        public Appendable append(char c) throws IOException {
          if (this.charsUntilSeparator == 0) {
            delegate.append(separator);
            this.charsUntilSeparator = afterEveryChars;
          } 
          delegate.append(c);
          this.charsUntilSeparator--;
          return this;
        }
        
        public Appendable append(@CheckForNull CharSequence chars, int off, int len) {
          throw new UnsupportedOperationException();
        }
        
        public Appendable append(@CheckForNull CharSequence chars) {
          throw new UnsupportedOperationException();
        }
      };
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  static Writer separatingWriter(final Writer delegate, String separator, int afterEveryChars) {
    final Appendable separatingAppendable = separatingAppendable(delegate, separator, afterEveryChars);
    return new Writer() {
        public void write(int c) throws IOException {
          separatingAppendable.append((char)c);
        }
        
        public void write(char[] chars, int off, int len) throws IOException {
          throw new UnsupportedOperationException();
        }
        
        public void flush() throws IOException {
          delegate.flush();
        }
        
        public void close() throws IOException {
          delegate.close();
        }
      };
  }
  
  static final class SeparatedBaseEncoding extends BaseEncoding {
    private final BaseEncoding delegate;
    
    private final String separator;
    
    private final int afterEveryChars;
    
    SeparatedBaseEncoding(BaseEncoding delegate, String separator, int afterEveryChars) {
      this.delegate = (BaseEncoding)Preconditions.checkNotNull(delegate);
      this.separator = (String)Preconditions.checkNotNull(separator);
      this.afterEveryChars = afterEveryChars;
      Preconditions.checkArgument((afterEveryChars > 0), "Cannot add a separator after every %s chars", afterEveryChars);
    }
    
    CharSequence trimTrailingPadding(CharSequence chars) {
      return this.delegate.trimTrailingPadding(chars);
    }
    
    int maxEncodedSize(int bytes) {
      int unseparatedSize = this.delegate.maxEncodedSize(bytes);
      return unseparatedSize + this.separator
        .length() * IntMath.divide(Math.max(0, unseparatedSize - 1), this.afterEveryChars, RoundingMode.FLOOR);
    }
    
    @J2ktIncompatible
    @GwtIncompatible
    public OutputStream encodingStream(Writer output) {
      return this.delegate.encodingStream(separatingWriter(output, this.separator, this.afterEveryChars));
    }
    
    void encodeTo(Appendable target, byte[] bytes, int off, int len) throws IOException {
      this.delegate.encodeTo(separatingAppendable(target, this.separator, this.afterEveryChars), bytes, off, len);
    }
    
    int maxDecodedSize(int chars) {
      return this.delegate.maxDecodedSize(chars);
    }
    
    public boolean canDecode(CharSequence chars) {
      StringBuilder builder = new StringBuilder();
      for (int i = 0; i < chars.length(); i++) {
        char c = chars.charAt(i);
        if (this.separator.indexOf(c) < 0)
          builder.append(c); 
      } 
      return this.delegate.canDecode(builder);
    }
    
    int decodeTo(byte[] target, CharSequence chars) throws BaseEncoding.DecodingException {
      StringBuilder stripped = new StringBuilder(chars.length());
      for (int i = 0; i < chars.length(); i++) {
        char c = chars.charAt(i);
        if (this.separator.indexOf(c) < 0)
          stripped.append(c); 
      } 
      return this.delegate.decodeTo(target, stripped);
    }
    
    @J2ktIncompatible
    @GwtIncompatible
    public InputStream decodingStream(Reader reader) {
      return this.delegate.decodingStream(ignoringReader(reader, this.separator));
    }
    
    public BaseEncoding omitPadding() {
      return this.delegate.omitPadding().withSeparator(this.separator, this.afterEveryChars);
    }
    
    public BaseEncoding withPadChar(char padChar) {
      return this.delegate.withPadChar(padChar).withSeparator(this.separator, this.afterEveryChars);
    }
    
    public BaseEncoding withSeparator(String separator, int afterEveryChars) {
      throw new UnsupportedOperationException("Already have a separator");
    }
    
    public BaseEncoding upperCase() {
      return this.delegate.upperCase().withSeparator(this.separator, this.afterEveryChars);
    }
    
    public BaseEncoding lowerCase() {
      return this.delegate.lowerCase().withSeparator(this.separator, this.afterEveryChars);
    }
    
    public BaseEncoding ignoreCase() {
      return this.delegate.ignoreCase().withSeparator(this.separator, this.afterEveryChars);
    }
    
    public String toString() {
      return this.delegate + ".withSeparator(\"" + this.separator + "\", " + this.afterEveryChars + ")";
    }
  }
}
