package me.syncwrld.booter.libs.google.guava.graph;

import java.util.Iterator;
import java.util.Set;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
interface GraphConnections<N, V> {
  Set<N> adjacentNodes();
  
  Set<N> predecessors();
  
  Set<N> successors();
  
  Iterator<EndpointPair<N>> incidentEdgeIterator(N paramN);
  
  @CheckForNull
  V value(N paramN);
  
  void removePredecessor(N paramN);
  
  @CheckForNull
  @CanIgnoreReturnValue
  V removeSuccessor(N paramN);
  
  void addPredecessor(N paramN, V paramV);
  
  @CheckForNull
  @CanIgnoreReturnValue
  V addSuccessor(N paramN, V paramV);
}
