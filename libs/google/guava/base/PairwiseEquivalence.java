package me.syncwrld.booter.libs.google.guava.base;

import java.io.Serializable;
import java.util.Iterator;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible(serializable = true)
final class PairwiseEquivalence<E, T extends E> extends Equivalence<Iterable<T>> implements Serializable {
  final Equivalence<E> elementEquivalence;
  
  private static final long serialVersionUID = 1L;
  
  PairwiseEquivalence(Equivalence<E> elementEquivalence) {
    this.elementEquivalence = Preconditions.<Equivalence<E>>checkNotNull(elementEquivalence);
  }
  
  protected boolean doEquivalent(Iterable<T> iterableA, Iterable<T> iterableB) {
    Iterator<T> iteratorA = iterableA.iterator();
    Iterator<T> iteratorB = iterableB.iterator();
    while (iteratorA.hasNext() && iteratorB.hasNext()) {
      if (!this.elementEquivalence.equivalent((E)iteratorA.next(), (E)iteratorB.next()))
        return false; 
    } 
    return (!iteratorA.hasNext() && !iteratorB.hasNext());
  }
  
  protected int doHash(Iterable<T> iterable) {
    int hash = 78721;
    for (T element : iterable)
      hash = hash * 24943 + this.elementEquivalence.hash((E)element); 
    return hash;
  }
  
  public boolean equals(@CheckForNull Object object) {
    if (object instanceof PairwiseEquivalence) {
      PairwiseEquivalence<Object, Object> that = (PairwiseEquivalence<Object, Object>)object;
      return this.elementEquivalence.equals(that.elementEquivalence);
    } 
    return false;
  }
  
  public int hashCode() {
    return this.elementEquivalence.hashCode() ^ 0x46A3EB07;
  }
  
  public String toString() {
    return this.elementEquivalence + ".pairwise()";
  }
}
