package me.syncwrld.booter.libs.google.guava.graph;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.guava.collect.ImmutableSet;
import me.syncwrld.booter.libs.google.guava.collect.Iterators;
import me.syncwrld.booter.libs.google.guava.collect.UnmodifiableIterator;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
final class EdgesConnecting<E> extends AbstractSet<E> {
  private final Map<?, E> nodeToOutEdge;
  
  private final Object targetNode;
  
  EdgesConnecting(Map<?, E> nodeToEdgeMap, Object targetNode) {
    this.nodeToOutEdge = (Map<?, E>)Preconditions.checkNotNull(nodeToEdgeMap);
    this.targetNode = Preconditions.checkNotNull(targetNode);
  }
  
  public UnmodifiableIterator<E> iterator() {
    E connectingEdge = getConnectingEdge();
    return (connectingEdge == null) ? 
      ImmutableSet.of().iterator() : 
      Iterators.singletonIterator(connectingEdge);
  }
  
  public int size() {
    return (getConnectingEdge() == null) ? 0 : 1;
  }
  
  public boolean contains(@CheckForNull Object edge) {
    E connectingEdge = getConnectingEdge();
    return (connectingEdge != null && connectingEdge.equals(edge));
  }
  
  @CheckForNull
  private E getConnectingEdge() {
    return this.nodeToOutEdge.get(this.targetNode);
  }
}
