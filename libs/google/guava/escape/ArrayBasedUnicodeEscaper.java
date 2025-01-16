package me.syncwrld.booter.libs.google.guava.escape;

import java.util.Map;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class ArrayBasedUnicodeEscaper extends UnicodeEscaper {
  private final char[][] replacements;
  
  private final int replacementsLength;
  
  private final int safeMin;
  
  private final int safeMax;
  
  private final char safeMinChar;
  
  private final char safeMaxChar;
  
  protected ArrayBasedUnicodeEscaper(Map<Character, String> replacementMap, int safeMin, int safeMax, String unsafeReplacement) {
    this(ArrayBasedEscaperMap.create(replacementMap), safeMin, safeMax, unsafeReplacement);
  }
  
  protected ArrayBasedUnicodeEscaper(ArrayBasedEscaperMap escaperMap, int safeMin, int safeMax, String unsafeReplacement) {
    Preconditions.checkNotNull(escaperMap);
    this.replacements = escaperMap.getReplacementArray();
    this.replacementsLength = this.replacements.length;
    if (safeMax < safeMin) {
      safeMax = -1;
      safeMin = Integer.MAX_VALUE;
    } 
    this.safeMin = safeMin;
    this.safeMax = safeMax;
    if (safeMin >= 55296) {
      this.safeMinChar = Character.MAX_VALUE;
      this.safeMaxChar = Character.MIN_VALUE;
    } else {
      this.safeMinChar = (char)safeMin;
      this.safeMaxChar = (char)Math.min(safeMax, 55295);
    } 
  }
  
  public final String escape(String s) {
    Preconditions.checkNotNull(s);
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if ((c < this.replacementsLength && this.replacements[c] != null) || c > this.safeMaxChar || c < this.safeMinChar)
        return escapeSlow(s, i); 
    } 
    return s;
  }
  
  @CheckForNull
  protected final char[] escape(int cp) {
    if (cp < this.replacementsLength) {
      char[] chars = this.replacements[cp];
      if (chars != null)
        return chars; 
    } 
    if (cp >= this.safeMin && cp <= this.safeMax)
      return null; 
    return escapeUnsafe(cp);
  }
  
  protected final int nextEscapeIndex(CharSequence csq, int index, int end) {
    while (index < end) {
      char c = csq.charAt(index);
      if ((c < this.replacementsLength && this.replacements[c] != null) || c > this.safeMaxChar || c < this.safeMinChar)
        break; 
      index++;
    } 
    return index;
  }
  
  @CheckForNull
  protected abstract char[] escapeUnsafe(int paramInt);
}
