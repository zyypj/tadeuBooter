package me.syncwrld.booter.libs.google.guava.graph;

import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.Beta;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@Beta
public interface MutableValueGraph<N, V> extends ValueGraph<N, V> {
  @CanIgnoreReturnValue
  boolean addNode(N paramN);
  
  @CheckForNull
  @CanIgnoreReturnValue
  V putEdgeValue(N paramN1, N paramN2, V paramV);
  
  @CheckForNull
  @CanIgnoreReturnValue
  V putEdgeValue(EndpointPair<N> paramEndpointPair, V paramV);
  
  @CanIgnoreReturnValue
  boolean removeNode(N paramN);
  
  @CheckForNull
  @CanIgnoreReturnValue
  V removeEdge(N paramN1, N paramN2);
  
  @CheckForNull
  @CanIgnoreReturnValue
  V removeEdge(EndpointPair<N> paramEndpointPair);
}
