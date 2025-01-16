package me.syncwrld.booter.libs.google.guava.graph;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import me.syncwrld.booter.libs.google.guava.collect.BiMap;
import me.syncwrld.booter.libs.google.guava.collect.HashBiMap;
import me.syncwrld.booter.libs.google.guava.collect.ImmutableBiMap;

@ElementTypesAreNonnullByDefault
final class UndirectedNetworkConnections<N, E> extends AbstractUndirectedNetworkConnections<N, E> {
  UndirectedNetworkConnections(Map<E, N> incidentEdgeMap) {
    super(incidentEdgeMap);
  }
  
  static <N, E> UndirectedNetworkConnections<N, E> of() {
    return new UndirectedNetworkConnections<>((Map<E, N>)HashBiMap.create(2));
  }
  
  static <N, E> UndirectedNetworkConnections<N, E> ofImmutable(Map<E, N> incidentEdges) {
    return new UndirectedNetworkConnections<>((Map<E, N>)ImmutableBiMap.copyOf(incidentEdges));
  }
  
  public Set<N> adjacentNodes() {
    return Collections.unmodifiableSet(((BiMap)this.incidentEdgeMap).values());
  }
  
  public Set<E> edgesConnecting(N node) {
    return new EdgesConnecting<>((Map<?, E>)((BiMap)this.incidentEdgeMap).inverse(), node);
  }
}
