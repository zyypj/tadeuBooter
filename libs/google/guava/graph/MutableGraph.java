package me.syncwrld.booter.libs.google.guava.graph;

import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.Beta;

@ElementTypesAreNonnullByDefault
@Beta
public interface MutableGraph<N> extends Graph<N> {
  @CanIgnoreReturnValue
  boolean addNode(N paramN);
  
  @CanIgnoreReturnValue
  boolean putEdge(N paramN1, N paramN2);
  
  @CanIgnoreReturnValue
  boolean putEdge(EndpointPair<N> paramEndpointPair);
  
  @CanIgnoreReturnValue
  boolean removeNode(N paramN);
  
  @CanIgnoreReturnValue
  boolean removeEdge(N paramN1, N paramN2);
  
  @CanIgnoreReturnValue
  boolean removeEdge(EndpointPair<N> paramEndpointPair);
}
