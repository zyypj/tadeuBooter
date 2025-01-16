package me.syncwrld.booter.libs.google.guava.graph;

import java.util.Set;
import me.syncwrld.booter.libs.google.errorprone.annotations.DoNotMock;
import me.syncwrld.booter.libs.google.guava.annotations.Beta;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@DoNotMock("Use GraphBuilder to create a real instance")
@ElementTypesAreNonnullByDefault
@Beta
public interface Graph<N> extends BaseGraph<N> {
  Set<N> nodes();
  
  Set<EndpointPair<N>> edges();
  
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
  
  boolean equals(@CheckForNull Object paramObject);
  
  int hashCode();
}
