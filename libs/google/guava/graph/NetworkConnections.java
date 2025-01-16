package me.syncwrld.booter.libs.google.guava.graph;

import java.util.Set;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
interface NetworkConnections<N, E> {
  Set<N> adjacentNodes();
  
  Set<N> predecessors();
  
  Set<N> successors();
  
  Set<E> incidentEdges();
  
  Set<E> inEdges();
  
  Set<E> outEdges();
  
  Set<E> edgesConnecting(N paramN);
  
  N adjacentNode(E paramE);
  
  @CheckForNull
  @CanIgnoreReturnValue
  N removeInEdge(E paramE, boolean paramBoolean);
  
  @CanIgnoreReturnValue
  N removeOutEdge(E paramE);
  
  void addInEdge(E paramE, N paramN, boolean paramBoolean);
  
  void addOutEdge(E paramE, N paramN);
}
