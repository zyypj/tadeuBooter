package me.syncwrld.booter.libs.google.guava.escape;

import me.syncwrld.booter.libs.google.errorprone.annotations.DoNotMock;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.base.Function;

@DoNotMock("Use Escapers.nullEscaper() or another methods from the *Escapers classes")
@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class Escaper {
  private final Function<String, String> asFunction = this::escape;
  
  public abstract String escape(String paramString);
  
  public final Function<String, String> asFunction() {
    return this.asFunction;
  }
}
