package me.syncwrld.booter.libs.google.guava.graph;

import java.util.Optional;
import java.util.Set;
import me.syncwrld.booter.libs.google.guava.annotations.Beta;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@Beta
public interface ValueGraph<N, V> extends BaseGraph<N> {
  Set<N> nodes();
  
  Set<EndpointPair<N>> edges();
  
  Graph<N> asGraph();
  
  boolean isDirected();
  
  boolean allowsSelfLoops();
  
  ElementOrder<N> nodeOrder();
  
  ElementOrder<N> incidentEdgeOrder();
  
  Set<N> adjacentNodes(N paramN);
  
  Set<N> predecessors(N paramN);
  
  Set<N> successors(N paramN);
  
  Set<EndpointPair<N>> incidentEdges(N paramN);
  
  int degree(N paramN);
  
  int inDegree(N paramN);
  
  int outDegree(N paramN);
  
  boolean hasEdgeConnecting(N paramN1, N paramN2);
  
  boolean hasEdgeConnecting(EndpointPair<N> paramEndpointPair);
  
  Optional<V> edgeValue(N paramN1, N paramN2);
  
  Optional<V> edgeValue(EndpointPair<N> paramEndpointPair);
  
  @CheckForNull
  V edgeValueOrDefault(N paramN1, N paramN2, @CheckForNull V paramV);
  
  @CheckForNull
  V edgeValueOrDefault(EndpointPair<N> paramEndpointPair, @CheckForNull V paramV);
  
  boolean equals(@CheckForNull Object paramObject);
  
  int hashCode();
}
