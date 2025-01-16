package me.syncwrld.booter.libs.google.guava.graph;

import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.Beta;

@ElementTypesAreNonnullByDefault
@Beta
public interface MutableNetwork<N, E> extends Network<N, E> {
  @CanIgnoreReturnValue
  boolean addNode(N paramN);
  
  @CanIgnoreReturnValue
  boolean addEdge(N paramN1, N paramN2, E paramE);
  
  @CanIgnoreReturnValue
  boolean addEdge(EndpointPair<N> paramEndpointPair, E paramE);
  
  @CanIgnoreReturnValue
  boolean removeNode(N paramN);
  
  @CanIgnoreReturnValue
  boolean removeEdge(E paramE);
}
