package me.syncwrld.booter.libs.google.guava.collect;

import java.io.Serializable;
import me.syncwrld.booter.libs.google.errorprone.annotations.concurrent.LazyInit;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible(serializable = true)
final class NaturalOrdering extends Ordering<Comparable<?>> implements Serializable {
  static final NaturalOrdering INSTANCE = new NaturalOrdering();
  
  @LazyInit
  @CheckForNull
  private transient Ordering<Comparable<?>> nullsFirst;
  
  @LazyInit
  @CheckForNull
  private transient Ordering<Comparable<?>> nullsLast;
  
  private static final long serialVersionUID = 0L;
  
  public int compare(Comparable<?> left, Comparable<?> right) {
    Preconditions.checkNotNull(left);
    Preconditions.checkNotNull(right);
    return left.compareTo(right);
  }
  
  public <S extends Comparable<?>> Ordering<S> nullsFirst() {
    Ordering<Comparable<?>> result = this.nullsFirst;
    if (result == null)
      result = this.nullsFirst = super.<Comparable<?>>nullsFirst(); 
    return (Ordering)result;
  }
  
  public <S extends Comparable<?>> Ordering<S> nullsLast() {
    Ordering<Comparable<?>> result = this.nullsLast;
    if (result == null)
      result = this.nullsLast = super.<Comparable<?>>nullsLast(); 
    return (Ordering)result;
  }
  
  public <S extends Comparable<?>> Ordering<S> reverse() {
    return ReverseNaturalOrdering.INSTANCE;
  }
  
  private Object readResolve() {
    return INSTANCE;
  }
  
  public String toString() {
    return "Ordering.natural()";
  }
}
