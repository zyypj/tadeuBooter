package me.syncwrld.booter.libs.google.guava.collect;

import java.io.Serializable;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;

@ElementTypesAreNonnullByDefault
@GwtCompatible(serializable = true)
final class UsingToStringOrdering extends Ordering<Object> implements Serializable {
  static final UsingToStringOrdering INSTANCE = new UsingToStringOrdering();
  
  private static final long serialVersionUID = 0L;
  
  public int compare(Object left, Object right) {
    return left.toString().compareTo(right.toString());
  }
  
  private Object readResolve() {
    return INSTANCE;
  }
  
  public String toString() {
    return "Ordering.usingToString()";
  }
}
