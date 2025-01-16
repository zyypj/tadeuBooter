package me.syncwrld.booter.libs.google.thirdparty.publicsuffix;

import java.util.Deque;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.annotations.VisibleForTesting;
import me.syncwrld.booter.libs.google.guava.base.Joiner;
import me.syncwrld.booter.libs.google.guava.collect.ImmutableMap;
import me.syncwrld.booter.libs.google.guava.collect.Queues;

@GwtCompatible
final class TrieParser {
  private static final Joiner DIRECT_JOINER = Joiner.on("");
  
  static ImmutableMap<String, PublicSuffixType> parseTrie(CharSequence... encodedChunks) {
    String encoded = DIRECT_JOINER.join((Object[])encodedChunks);
    return parseFullString(encoded);
  }
  
  @VisibleForTesting
  static ImmutableMap<String, PublicSuffixType> parseFullString(String encoded) {
    ImmutableMap.Builder<String, PublicSuffixType> builder = ImmutableMap.builder();
    int encodedLen = encoded.length();
    int idx = 0;
    while (idx < encodedLen)
      idx += doParseTrieToBuilder(Queues.newArrayDeque(), encoded, idx, builder); 
    return builder.buildOrThrow();
  }
  
  private static int doParseTrieToBuilder(Deque<CharSequence> stack, CharSequence encoded, int start, ImmutableMap.Builder<String, PublicSuffixType> builder) {
    int encodedLen = encoded.length();
    int idx = start;
    char c = Character.MIN_VALUE;
    for (; idx < encodedLen; idx++) {
      c = encoded.charAt(idx);
      if (c == '&' || c == '?' || c == '!' || c == ':' || c == ',')
        break; 
    } 
    stack.push(reverse(encoded.subSequence(start, idx)));
    if (c == '!' || c == '?' || c == ':' || c == ',') {
      String domain = DIRECT_JOINER.join(stack);
      if (domain.length() > 0)
        builder.put(domain, PublicSuffixType.fromCode(c)); 
    } 
    idx++;
    if (c != '?' && c != ',')
      while (idx < encodedLen) {
        idx += doParseTrieToBuilder(stack, encoded, idx, builder);
        if (encoded.charAt(idx) == '?' || encoded.charAt(idx) == ',') {
          idx++;
          break;
        } 
      }  
    stack.pop();
    return idx - start;
  }
  
  private static CharSequence reverse(CharSequence s) {
    return (new StringBuilder(s)).reverse();
  }
}
