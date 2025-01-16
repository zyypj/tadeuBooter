package me.syncwrld.booter.libs.unnamed.inject.key;

import java.util.Objects;
import me.syncwrld.booter.libs.unnamed.inject.util.Validate;

public final class InjectedKey<T> {
  private final Key<T> key;
  
  private final boolean optional;
  
  private final boolean assisted;
  
  public InjectedKey(Key<T> key, boolean optional, boolean assisted) {
    this.key = (Key<T>)Validate.notNull(key, "key", new Object[0]);
    this.optional = optional;
    this.assisted = assisted;
  }
  
  public Key<T> getKey() {
    return this.key;
  }
  
  public boolean isOptional() {
    return this.optional;
  }
  
  public boolean isAssisted() {
    return this.assisted;
  }
  
  public String toString() {
    return (this.optional ? "(optional) " : "(required) ") + (this.assisted ? "(assisted) " : "") + this.key
      
      .toString();
  }
  
  public boolean equals(Object o) {
    if (this == o)
      return true; 
    if (o == null || getClass() != o.getClass())
      return false; 
    InjectedKey<?> that = (InjectedKey)o;
    return (this.optional == that.optional && this.assisted == that.assisted && this.key
      
      .equals(that.key));
  }
  
  public int hashCode() {
    return Objects.hash(new Object[] { Boolean.valueOf(this.optional), Boolean.valueOf(this.assisted), this.key });
  }
}
