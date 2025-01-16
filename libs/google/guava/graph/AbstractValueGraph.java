package me.syncwrld.booter.libs.google.guava.graph;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import me.syncwrld.booter.libs.google.guava.annotations.Beta;
import me.syncwrld.booter.libs.google.guava.collect.Maps;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@Beta
public abstract class AbstractValueGraph<N, V> extends AbstractBaseGraph<N> implements ValueGraph<N, V> {
  public Graph<N> asGraph() {
    return new AbstractGraph<N>() {
        public Set<N> nodes() {
          return AbstractValueGraph.this.nodes();
        }
        
        public Set<EndpointPair<N>> edges() {
          return AbstractValueGraph.this.edges();
        }
        
        public boolean isDirected() {
          return AbstractValueGraph.this.isDirected();
        }
        
        public boolean allowsSelfLoops() {
          return AbstractValueGraph.this.allowsSelfLoops();
        }
        
        public ElementOrder<N> nodeOrder() {
          return AbstractValueGraph.this.nodeOrder();
        }
        
        public ElementOrder<N> incidentEdgeOrder() {
          return AbstractValueGraph.this.incidentEdgeOrder();
        }
        
        public Set<N> adjacentNodes(N node) {
          return AbstractValueGraph.this.adjacentNodes(node);
        }
        
        public Set<N> predecessors(N node) {
          return AbstractValueGraph.this.predecessors(node);
        }
        
        public Set<N> successors(N node) {
          return AbstractValueGraph.this.successors(node);
        }
        
        public int degree(N node) {
          return AbstractValueGraph.this.degree(node);
        }
        
        public int inDegree(N node) {
          return AbstractValueGraph.this.inDegree(node);
        }
        
        public int outDegree(N node) {
          return AbstractValueGraph.this.outDegree(node);
        }
      };
  }
  
  public Optional<V> edgeValue(N nodeU, N nodeV) {
    return Optional.ofNullable(edgeValueOrDefault(nodeU, nodeV, null));
  }
  
  public Optional<V> edgeValue(EndpointPair<N> endpoints) {
    return Optional.ofNullable(edgeValueOrDefault(endpoints, null));
  }
  
  public final boolean equals(@CheckForNull Object obj) {
    if (obj == this)
      return true; 
    if (!(obj instanceof ValueGraph))
      return false; 
    ValueGraph<?, ?> other = (ValueGraph<?, ?>)obj;
    return (isDirected() == other.isDirected() && 
      nodes().equals(other.nodes()) && 
      edgeValueMap(this).equals(edgeValueMap(other)));
  }
  
  public final int hashCode() {
    return edgeValueMap(this).hashCode();
  }
  
  public String toString() {
    return "isDirected: " + 
      isDirected() + ", allowsSelfLoops: " + 
      
      allowsSelfLoops() + ", nodes: " + 
      
      nodes() + ", edges: " + 
      
      edgeValueMap(this);
  }
  
  private static <N, V> Map<EndpointPair<N>, V> edgeValueMap(ValueGraph<N, V> graph) {
    return Maps.asMap(graph
        .edges(), edge -> Objects.requireNonNull(graph.edgeValueOrDefault(edge.nodeU(), edge.nodeV(), null)));
  }
}
