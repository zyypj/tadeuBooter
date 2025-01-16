package me.syncwrld.booter.libs.google.guava.collect;

import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public enum BoundType {
  OPEN(false),
  CLOSED(true);
  
  final boolean inclusive;
  
  BoundType(boolean inclusive) {
    this.inclusive = inclusive;
  }
  
  static BoundType forBoolean(boolean inclusive) {
    return inclusive ? CLOSED : OPEN;
  }
}
