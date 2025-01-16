package me.syncwrld.booter.libs.google.guava.graph;

import java.util.AbstractSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.guava.collect.Iterables;
import me.syncwrld.booter.libs.google.guava.collect.Iterators;
import me.syncwrld.booter.libs.google.guava.collect.Sets;
import me.syncwrld.booter.libs.google.guava.collect.UnmodifiableIterator;
import me.syncwrld.booter.libs.google.guava.math.IntMath;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
abstract class AbstractDirectedNetworkConnections<N, E> implements NetworkConnections<N, E> {
  final Map<E, N> inEdgeMap;
  
  final Map<E, N> outEdgeMap;
  
  private int selfLoopCount;
  
  AbstractDirectedNetworkConnections(Map<E, N> inEdgeMap, Map<E, N> outEdgeMap, int selfLoopCount) {
    this.inEdgeMap = (Map<E, N>)Preconditions.checkNotNull(inEdgeMap);
    this.outEdgeMap = (Map<E, N>)Preconditions.checkNotNull(outEdgeMap);
    this.selfLoopCount = Graphs.checkNonNegative(selfLoopCount);
    Preconditions.checkState((selfLoopCount <= inEdgeMap.size() && selfLoopCount <= outEdgeMap.size()));
  }
  
  public Set<N> adjacentNodes() {
    return (Set<N>)Sets.union(predecessors(), successors());
  }
  
  public Set<E> incidentEdges() {
    return new AbstractSet<E>() {
        public UnmodifiableIterator<E> iterator() {
          Iterable<E> incidentEdges = (AbstractDirectedNetworkConnections.this.selfLoopCount == 0) ? Iterables.concat(AbstractDirectedNetworkConnections.this.inEdgeMap.keySet(), AbstractDirectedNetworkConnections.this.outEdgeMap.keySet()) : (Iterable<E>)Sets.union(AbstractDirectedNetworkConnections.this.inEdgeMap.keySet(), AbstractDirectedNetworkConnections.this.outEdgeMap.keySet());
          return Iterators.unmodifiableIterator(incidentEdges.iterator());
        }
        
        public int size() {
          return IntMath.saturatedAdd(AbstractDirectedNetworkConnections.this.inEdgeMap.size(), AbstractDirectedNetworkConnections.this.outEdgeMap.size() - AbstractDirectedNetworkConnections.this.selfLoopCount);
        }
        
        public boolean contains(@CheckForNull Object obj) {
          return (AbstractDirectedNetworkConnections.this.inEdgeMap.containsKey(obj) || AbstractDirectedNetworkConnections.this.outEdgeMap.containsKey(obj));
        }
      };
  }
  
  public Set<E> inEdges() {
    return Collections.unmodifiableSet(this.inEdgeMap.keySet());
  }
  
  public Set<E> outEdges() {
    return Collections.unmodifiableSet(this.outEdgeMap.keySet());
  }
  
  public N adjacentNode(E edge) {
    return Objects.requireNonNull(this.outEdgeMap.get(edge));
  }
  
  public N removeInEdge(E edge, boolean isSelfLoop) {
    if (isSelfLoop)
      Graphs.checkNonNegative(--this.selfLoopCount); 
    N previousNode = this.inEdgeMap.remove(edge);
    return Objects.requireNonNull(previousNode);
  }
  
  public N removeOutEdge(E edge) {
    N previousNode = this.outEdgeMap.remove(edge);
    return Objects.requireNonNull(previousNode);
  }
  
  public void addInEdge(E edge, N node, boolean isSelfLoop) {
    Preconditions.checkNotNull(edge);
    Preconditions.checkNotNull(node);
    if (isSelfLoop)
      Graphs.checkPositive(++this.selfLoopCount); 
    N previousNode = this.inEdgeMap.put(edge, node);
    Preconditions.checkState((previousNode == null));
  }
  
  public void addOutEdge(E edge, N node) {
    Preconditions.checkNotNull(edge);
    Preconditions.checkNotNull(node);
    N previousNode = this.outEdgeMap.put(edge, node);
    Preconditions.checkState((previousNode == null));
  }
}
