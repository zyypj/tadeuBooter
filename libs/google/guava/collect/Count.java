package me.syncwrld.booter.libs.google.guava.collect;

import java.io.Serializable;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
final class Count implements Serializable {
  private int value;
  
  Count(int value) {
    this.value = value;
  }
  
  public int get() {
    return this.value;
  }
  
  public void add(int delta) {
    this.value += delta;
  }
  
  public int addAndGet(int delta) {
    return this.value += delta;
  }
  
  public void set(int newValue) {
    this.value = newValue;
  }
  
  public int getAndSet(int newValue) {
    int result = this.value;
    this.value = newValue;
    return result;
  }
  
  public int hashCode() {
    return this.value;
  }
  
  public boolean equals(@CheckForNull Object obj) {
    return (obj instanceof Count && ((Count)obj).value == this.value);
  }
  
  public String toString() {
    return Integer.toString(this.value);
  }
}
