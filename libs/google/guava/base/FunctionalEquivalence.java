package me.syncwrld.booter.libs.google.guava.base;

import java.io.Serializable;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
final class FunctionalEquivalence<F, T> extends Equivalence<F> implements Serializable {
  private static final long serialVersionUID = 0L;
  
  private final Function<? super F, ? extends T> function;
  
  private final Equivalence<T> resultEquivalence;
  
  FunctionalEquivalence(Function<? super F, ? extends T> function, Equivalence<T> resultEquivalence) {
    this.function = Preconditions.<Function<? super F, ? extends T>>checkNotNull(function);
    this.resultEquivalence = Preconditions.<Equivalence<T>>checkNotNull(resultEquivalence);
  }
  
  protected boolean doEquivalent(F a, F b) {
    return this.resultEquivalence.equivalent(this.function.apply(a), this.function.apply(b));
  }
  
  protected int doHash(F a) {
    return this.resultEquivalence.hash(this.function.apply(a));
  }
  
  public boolean equals(@CheckForNull Object obj) {
    if (obj == this)
      return true; 
    if (obj instanceof FunctionalEquivalence) {
      FunctionalEquivalence<?, ?> that = (FunctionalEquivalence<?, ?>)obj;
      return (this.function.equals(that.function) && this.resultEquivalence.equals(that.resultEquivalence));
    } 
    return false;
  }
  
  public int hashCode() {
    return Objects.hashCode(new Object[] { this.function, this.resultEquivalence });
  }
  
  public String toString() {
    return this.resultEquivalence + ".onResultOf(" + this.function + ")";
  }
}
