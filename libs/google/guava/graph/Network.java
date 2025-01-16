package me.syncwrld.booter.libs.google.guava.graph;

import java.util.Optional;
import java.util.Set;
import me.syncwrld.booter.libs.google.errorprone.annotations.DoNotMock;
import me.syncwrld.booter.libs.google.guava.annotations.Beta;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@DoNotMock("Use NetworkBuilder to create a real instance")
@ElementTypesAreNonnullByDefault
@Beta
public interface Network<N, E> extends SuccessorsFunction<N>, PredecessorsFunction<N> {
  Set<N> nodes();
  
  Set<E> edges();
  
  Graph<N> asGraph();
  
  boolean isDirected();
  
  boolean allowsParallelEdges();
  
  boolean allowsSelfLoops();
  
  ElementOrder<N> nodeOrder();
  
  ElementOrder<E> edgeOrder();
  
  Set<N> adjacentNodes(N paramN);
  
  Set<N> predecessors(N paramN);
  
  Set<N> successors(N paramN);
  
  Set<E> incidentEdges(N paramN);
  
  Set<E> inEdges(N paramN);
  
  Set<E> outEdges(N paramN);
  
  int degree(N paramN);
  
  int inDegree(N paramN);
  
  int outDegree(N paramN);
  
  EndpointPair<N> incidentNodes(E paramE);
  
  Set<E> adjacentEdges(E paramE);
  
  Set<E> edgesConnecting(N paramN1, N paramN2);
  
  Set<E> edgesConnecting(EndpointPair<N> paramEndpointPair);
  
  Optional<E> edgeConnecting(N paramN1, N paramN2);
  
  Optional<E> edgeConnecting(EndpointPair<N> paramEndpointPair);
  
  @CheckForNull
  E edgeConnectingOrNull(N paramN1, N paramN2);
  
  @CheckForNull
  E edgeConnectingOrNull(EndpointPair<N> paramEndpointPair);
  
  boolean hasEdgeConnecting(N paramN1, N paramN2);
  
  boolean hasEdgeConnecting(EndpointPair<N> paramEndpointPair);
  
  boolean equals(@CheckForNull Object paramObject);
  
  int hashCode();
}
