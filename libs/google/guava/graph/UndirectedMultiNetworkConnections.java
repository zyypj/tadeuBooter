package me.syncwrld.booter.libs.google.guava.graph;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import me.syncwrld.booter.libs.google.errorprone.annotations.concurrent.LazyInit;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.guava.collect.HashMultiset;
import me.syncwrld.booter.libs.google.guava.collect.ImmutableMap;
import me.syncwrld.booter.libs.google.guava.collect.Multiset;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
final class UndirectedMultiNetworkConnections<N, E> extends AbstractUndirectedNetworkConnections<N, E> {
  @CheckForNull
  @LazyInit
  private transient Reference<Multiset<N>> adjacentNodesReference;
  
  private UndirectedMultiNetworkConnections(Map<E, N> incidentEdges) {
    super(incidentEdges);
  }
  
  static <N, E> UndirectedMultiNetworkConnections<N, E> of() {
    return new UndirectedMultiNetworkConnections<>(new HashMap<>(2, 1.0F));
  }
  
  static <N, E> UndirectedMultiNetworkConnections<N, E> ofImmutable(Map<E, N> incidentEdges) {
    return new UndirectedMultiNetworkConnections<>((Map<E, N>)ImmutableMap.copyOf(incidentEdges));
  }
  
  public Set<N> adjacentNodes() {
    return Collections.unmodifiableSet(adjacentNodesMultiset().elementSet());
  }
  
  private Multiset<N> adjacentNodesMultiset() {
    HashMultiset hashMultiset;
    Multiset<N> adjacentNodes = getReference(this.adjacentNodesReference);
    if (adjacentNodes == null) {
      hashMultiset = HashMultiset.create(this.incidentEdgeMap.values());
      this.adjacentNodesReference = new SoftReference(hashMultiset);
    } 
    return (Multiset<N>)hashMultiset;
  }
  
  public Set<E> edgesConnecting(final N node) {
    return new MultiEdgesConnecting<E>(this.incidentEdgeMap, node) {
        public int size() {
          return UndirectedMultiNetworkConnections.this.adjacentNodesMultiset().count(node);
        }
      };
  }
  
  @CheckForNull
  public N removeInEdge(E edge, boolean isSelfLoop) {
    if (!isSelfLoop)
      return removeOutEdge(edge); 
    return null;
  }
  
  public N removeOutEdge(E edge) {
    N node = super.removeOutEdge(edge);
    Multiset<N> adjacentNodes = getReference(this.adjacentNodesReference);
    if (adjacentNodes != null)
      Preconditions.checkState(adjacentNodes.remove(node)); 
    return node;
  }
  
  public void addInEdge(E edge, N node, boolean isSelfLoop) {
    if (!isSelfLoop)
      addOutEdge(edge, node); 
  }
  
  public void addOutEdge(E edge, N node) {
    super.addOutEdge(edge, node);
    Multiset<N> adjacentNodes = getReference(this.adjacentNodesReference);
    if (adjacentNodes != null)
      Preconditions.checkState(adjacentNodes.add(node)); 
  }
  
  @CheckForNull
  private static <T> T getReference(@CheckForNull Reference<T> reference) {
    return (reference == null) ? null : reference.get();
  }
}
