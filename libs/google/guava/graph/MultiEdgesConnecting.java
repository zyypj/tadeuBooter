package me.syncwrld.booter.libs.google.guava.graph;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.guava.collect.AbstractIterator;
import me.syncwrld.booter.libs.google.guava.collect.UnmodifiableIterator;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
abstract class MultiEdgesConnecting<E> extends AbstractSet<E> {
  private final Map<E, ?> outEdgeToNode;
  
  private final Object targetNode;
  
  MultiEdgesConnecting(Map<E, ?> outEdgeToNode, Object targetNode) {
    this.outEdgeToNode = (Map<E, ?>)Preconditions.checkNotNull(outEdgeToNode);
    this.targetNode = Preconditions.checkNotNull(targetNode);
  }
  
  public UnmodifiableIterator<E> iterator() {
    final Iterator<? extends Map.Entry<E, ?>> entries = this.outEdgeToNode.entrySet().iterator();
    return (UnmodifiableIterator<E>)new AbstractIterator<E>() {
        @CheckForNull
        protected E computeNext() {
          while (entries.hasNext()) {
            Map.Entry<E, ?> entry = entries.next();
            if (MultiEdgesConnecting.this.targetNode.equals(entry.getValue()))
              return entry.getKey(); 
          } 
          return (E)endOfData();
        }
      };
  }
  
  public boolean contains(@CheckForNull Object edge) {
    return this.targetNode.equals(this.outEdgeToNode.get(edge));
  }
}
