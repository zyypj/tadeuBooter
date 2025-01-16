package me.syncwrld.booter.libs.google.guava.graph;

import java.util.AbstractSet;
import java.util.Set;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
abstract class IncidentEdgeSet<N> extends AbstractSet<EndpointPair<N>> {
  final N node;
  
  final BaseGraph<N> graph;
  
  IncidentEdgeSet(BaseGraph<N> graph, N node) {
    this.graph = graph;
    this.node = node;
  }
  
  public boolean remove(@CheckForNull Object o) {
    throw new UnsupportedOperationException();
  }
  
  public int size() {
    if (this.graph.isDirected())
      return this.graph.inDegree(this.node) + this.graph
        .outDegree(this.node) - (
        this.graph.successors(this.node).contains(this.node) ? 1 : 0); 
    return this.graph.adjacentNodes(this.node).size();
  }
  
  public boolean contains(@CheckForNull Object obj) {
    if (!(obj instanceof EndpointPair))
      return false; 
    EndpointPair<?> endpointPair = (EndpointPair)obj;
    if (this.graph.isDirected()) {
      if (!endpointPair.isOrdered())
        return false; 
      Object source = endpointPair.source();
      Object target = endpointPair.target();
      return ((this.node.equals(source) && this.graph.successors(this.node).contains(target)) || (this.node
        .equals(target) && this.graph.predecessors(this.node).contains(source)));
    } 
    if (endpointPair.isOrdered())
      return false; 
    Set<N> adjacent = this.graph.adjacentNodes(this.node);
    Object nodeU = endpointPair.nodeU();
    Object nodeV = endpointPair.nodeV();
    return ((this.node.equals(nodeV) && adjacent.contains(nodeU)) || (this.node
      .equals(nodeU) && adjacent.contains(nodeV)));
  }
}
