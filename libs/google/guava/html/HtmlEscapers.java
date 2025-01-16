package me.syncwrld.booter.libs.google.guava.html;

import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.escape.Escaper;
import me.syncwrld.booter.libs.google.guava.escape.Escapers;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public final class HtmlEscapers {
  public static Escaper htmlEscaper() {
    return HTML_ESCAPER;
  }
  
  private static final Escaper HTML_ESCAPER = Escapers.builder()
    .addEscape('"', "&quot;")
    
    .addEscape('\'', "&#39;")
    .addEscape('&', "&amp;")
    .addEscape('<', "&lt;")
    .addEscape('>', "&gt;")
    .build();
}
