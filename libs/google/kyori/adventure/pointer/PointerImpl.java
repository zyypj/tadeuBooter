package me.syncwrld.booter.libs.google.kyori.adventure.pointer;

import me.syncwrld.booter.libs.google.kyori.adventure.internal.Internals;
import me.syncwrld.booter.libs.google.kyori.adventure.key.Key;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

final class PointerImpl<T> implements Pointer<T> {
  private final Class<T> type;
  
  private final Key key;
  
  PointerImpl(Class<T> type, Key key) {
    this.type = type;
    this.key = key;
  }
  
  @NotNull
  public Class<T> type() {
    return this.type;
  }
  
  @NotNull
  public Key key() {
    return this.key;
  }
  
  public String toString() {
    return Internals.toString(this);
  }
  
  public boolean equals(@Nullable Object other) {
    if (this == other)
      return true; 
    if (other == null || getClass() != other.getClass())
      return false; 
    PointerImpl<?> that = (PointerImpl)other;
    return (this.type.equals(that.type) && this.key.equals(that.key));
  }
  
  public int hashCode() {
    int result = this.type.hashCode();
    result = 31 * result + this.key.hashCode();
    return result;
  }
}
