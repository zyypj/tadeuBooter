package me.syncwrld.booter.libs.google.guava.collect;

import java.io.Serializable;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible(serializable = true)
final class NullsFirstOrdering<T> extends Ordering<T> implements Serializable {
  final Ordering<? super T> ordering;
  
  private static final long serialVersionUID = 0L;
  
  NullsFirstOrdering(Ordering<? super T> ordering) {
    this.ordering = ordering;
  }
  
  public int compare(@CheckForNull T left, @CheckForNull T right) {
    if (left == right)
      return 0; 
    if (left == null)
      return -1; 
    if (right == null)
      return 1; 
    return this.ordering.compare(left, right);
  }
  
  public <S extends T> Ordering<S> reverse() {
    return this.ordering.<T>reverse().nullsLast();
  }
  
  public <S extends T> Ordering<S> nullsFirst() {
    return this;
  }
  
  public <S extends T> Ordering<S> nullsLast() {
    return this.ordering.nullsLast();
  }
  
  public boolean equals(@CheckForNull Object object) {
    if (object == this)
      return true; 
    if (object instanceof NullsFirstOrdering) {
      NullsFirstOrdering<?> that = (NullsFirstOrdering)object;
      return this.ordering.equals(that.ordering);
    } 
    return false;
  }
  
  public int hashCode() {
    return this.ordering.hashCode() ^ 0x39153A74;
  }
  
  public String toString() {
    return this.ordering + ".nullsFirst()";
  }
}
