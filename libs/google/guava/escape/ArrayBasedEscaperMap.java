package me.syncwrld.booter.libs.google.guava.escape;

import java.util.Collections;
import java.util.Map;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.annotations.VisibleForTesting;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public final class ArrayBasedEscaperMap {
  private final char[][] replacementArray;
  
  public static ArrayBasedEscaperMap create(Map<Character, String> replacements) {
    return new ArrayBasedEscaperMap(createReplacementArray(replacements));
  }
  
  private ArrayBasedEscaperMap(char[][] replacementArray) {
    this.replacementArray = replacementArray;
  }
  
  char[][] getReplacementArray() {
    return this.replacementArray;
  }
  
  @VisibleForTesting
  static char[][] createReplacementArray(Map<Character, String> map) {
    Preconditions.checkNotNull(map);
    if (map.isEmpty())
      return EMPTY_REPLACEMENT_ARRAY; 
    char max = ((Character)Collections.<Character>max(map.keySet())).charValue();
    char[][] replacements = new char[max + 1][];
    for (Character c : map.keySet())
      replacements[c.charValue()] = ((String)map.get(c)).toCharArray(); 
    return replacements;
  }
  
  private static final char[][] EMPTY_REPLACEMENT_ARRAY = new char[0][0];
}
