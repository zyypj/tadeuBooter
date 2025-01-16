package me.syncwrld.booter.libs.google.guava.graph;

import java.util.Map;
import java.util.Set;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.errorprone.annotations.Immutable;
import me.syncwrld.booter.libs.google.guava.annotations.Beta;
import me.syncwrld.booter.libs.google.guava.base.Function;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.guava.collect.ImmutableMap;
import me.syncwrld.booter.libs.google.guava.collect.Maps;

@Immutable(containerOf = {"N", "E"})
@ElementTypesAreNonnullByDefault
@Beta
public final class ImmutableNetwork<N, E> extends StandardNetwork<N, E> {
  private ImmutableNetwork(Network<N, E> network) {
    super(
        NetworkBuilder.from(network), getNodeConnections(network), getEdgeToReferenceNode(network));
  }
  
  public static <N, E> ImmutableNetwork<N, E> copyOf(Network<N, E> network) {
    return (network instanceof ImmutableNetwork) ? 
      (ImmutableNetwork<N, E>)network : 
      new ImmutableNetwork<>(network);
  }
  
  @Deprecated
  public static <N, E> ImmutableNetwork<N, E> copyOf(ImmutableNetwork<N, E> network) {
    return (ImmutableNetwork<N, E>)Preconditions.checkNotNull(network);
  }
  
  public ImmutableGraph<N> asGraph() {
    return new ImmutableGraph<>(super.asGraph());
  }
  
  private static <N, E> Map<N, NetworkConnections<N, E>> getNodeConnections(Network<N, E> network) {
    ImmutableMap.Builder<N, NetworkConnections<N, E>> nodeConnections = ImmutableMap.builder();
    for (N node : network.nodes())
      nodeConnections.put(node, connectionsOf(network, node)); 
    return (Map<N, NetworkConnections<N, E>>)nodeConnections.buildOrThrow();
  }
  
  private static <N, E> Map<E, N> getEdgeToReferenceNode(Network<N, E> network) {
    ImmutableMap.Builder<E, N> edgeToReferenceNode = ImmutableMap.builder();
    for (E edge : network.edges())
      edgeToReferenceNode.put(edge, network.incidentNodes(edge).nodeU()); 
    return (Map<E, N>)edgeToReferenceNode.buildOrThrow();
  }
  
  private static <N, E> NetworkConnections<N, E> connectionsOf(Network<N, E> network, N node) {
    if (network.isDirected()) {
      Map<E, N> inEdgeMap = Maps.asMap(network.inEdges(node), sourceNodeFn(network));
      Map<E, N> outEdgeMap = Maps.asMap(network.outEdges(node), targetNodeFn(network));
      int selfLoopCount = network.edgesConnecting(node, node).size();
      return network.allowsParallelEdges() ? 
        DirectedMultiNetworkConnections.<N, E>ofImmutable(inEdgeMap, outEdgeMap, selfLoopCount) : 
        DirectedNetworkConnections.<N, E>ofImmutable(inEdgeMap, outEdgeMap, selfLoopCount);
    } 
    Map<E, N> incidentEdgeMap = Maps.asMap(network.incidentEdges(node), adjacentNodeFn(network, node));
    return network.allowsParallelEdges() ? 
      UndirectedMultiNetworkConnections.<N, E>ofImmutable(incidentEdgeMap) : 
      UndirectedNetworkConnections.<N, E>ofImmutable(incidentEdgeMap);
  }
  
  private static <N, E> Function<E, N> sourceNodeFn(Network<N, E> network) {
    return edge -> network.incidentNodes(edge).source();
  }
  
  private static <N, E> Function<E, N> targetNodeFn(Network<N, E> network) {
    return edge -> network.incidentNodes(edge).target();
  }
  
  private static <N, E> Function<E, N> adjacentNodeFn(Network<N, E> network, N node) {
    return edge -> network.incidentNodes(edge).adjacentNode(node);
  }
  
  public static class Builder<N, E> {
    private final MutableNetwork<N, E> mutableNetwork;
    
    Builder(NetworkBuilder<N, E> networkBuilder) {
      this.mutableNetwork = networkBuilder.build();
    }
    
    @CanIgnoreReturnValue
    public Builder<N, E> addNode(N node) {
      this.mutableNetwork.addNode(node);
      return this;
    }
    
    @CanIgnoreReturnValue
    public Builder<N, E> addEdge(N nodeU, N nodeV, E edge) {
      this.mutableNetwork.addEdge(nodeU, nodeV, edge);
      return this;
    }
    
    @CanIgnoreReturnValue
    public Builder<N, E> addEdge(EndpointPair<N> endpoints, E edge) {
      this.mutableNetwork.addEdge(endpoints, edge);
      return this;
    }
    
    public ImmutableNetwork<N, E> build() {
      return ImmutableNetwork.copyOf(this.mutableNetwork);
    }
  }
}
