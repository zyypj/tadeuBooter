package me.syncwrld.booter.libs.google.guava.collect;

import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class ForwardingObject {
  protected abstract Object delegate();
  
  public String toString() {
    return delegate().toString();
  }
}
