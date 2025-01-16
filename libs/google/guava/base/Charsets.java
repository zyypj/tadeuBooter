package me.syncwrld.booter.libs.google.guava.base;

import java.nio.charset.Charset;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;

@ElementTypesAreNonnullByDefault
@GwtCompatible(emulated = true)
public final class Charsets {
  @J2ktIncompatible
  @GwtIncompatible
  public static final Charset US_ASCII = Charset.forName("US-ASCII");
  
  public static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
  
  public static final Charset UTF_8 = Charset.forName("UTF-8");
  
  @J2ktIncompatible
  @GwtIncompatible
  public static final Charset UTF_16BE = Charset.forName("UTF-16BE");
  
  @J2ktIncompatible
  @GwtIncompatible
  public static final Charset UTF_16LE = Charset.forName("UTF-16LE");
  
  @J2ktIncompatible
  @GwtIncompatible
  public static final Charset UTF_16 = Charset.forName("UTF-16");
}
