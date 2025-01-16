package me.syncwrld.booter.libs.google.guava.graph;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
abstract class AbstractUndirectedNetworkConnections<N, E> implements NetworkConnections<N, E> {
  final Map<E, N> incidentEdgeMap;
  
  AbstractUndirectedNetworkConnections(Map<E, N> incidentEdgeMap) {
    this.incidentEdgeMap = (Map<E, N>)Preconditions.checkNotNull(incidentEdgeMap);
  }
  
  public Set<N> predecessors() {
    return adjacentNodes();
  }
  
  public Set<N> successors() {
    return adjacentNodes();
  }
  
  public Set<E> incidentEdges() {
    return Collections.unmodifiableSet(this.incidentEdgeMap.keySet());
  }
  
  public Set<E> inEdges() {
    return incidentEdges();
  }
  
  public Set<E> outEdges() {
    return incidentEdges();
  }
  
  public N adjacentNode(E edge) {
    return Objects.requireNonNull(this.incidentEdgeMap.get(edge));
  }
  
  @CheckForNull
  public N removeInEdge(E edge, boolean isSelfLoop) {
    if (!isSelfLoop)
      return removeOutEdge(edge); 
    return null;
  }
  
  public N removeOutEdge(E edge) {
    N previousNode = this.incidentEdgeMap.remove(edge);
    return Objects.requireNonNull(previousNode);
  }
  
  public void addInEdge(E edge, N node, boolean isSelfLoop) {
    if (!isSelfLoop)
      addOutEdge(edge, node); 
  }
  
  public void addOutEdge(E edge, N node) {
    N previousNode = this.incidentEdgeMap.put(edge, node);
    Preconditions.checkState((previousNode == null));
  }
}
