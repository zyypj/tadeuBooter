package me.syncwrld.booter.libs.google.guava.graph;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.errorprone.annotations.Immutable;
import me.syncwrld.booter.libs.google.guava.annotations.Beta;
import me.syncwrld.booter.libs.google.guava.base.Function;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.guava.collect.ImmutableMap;
import me.syncwrld.booter.libs.google.guava.collect.Maps;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@Immutable(containerOf = {"N", "V"})
@ElementTypesAreNonnullByDefault
@Beta
public final class ImmutableValueGraph<N, V> extends StandardValueGraph<N, V> {
  private ImmutableValueGraph(ValueGraph<N, V> graph) {
    super(ValueGraphBuilder.from(graph), (Map<N, GraphConnections<N, V>>)getNodeConnections(graph), graph.edges().size());
  }
  
  public static <N, V> ImmutableValueGraph<N, V> copyOf(ValueGraph<N, V> graph) {
    return (graph instanceof ImmutableValueGraph) ? 
      (ImmutableValueGraph<N, V>)graph : 
      new ImmutableValueGraph<>(graph);
  }
  
  @Deprecated
  public static <N, V> ImmutableValueGraph<N, V> copyOf(ImmutableValueGraph<N, V> graph) {
    return (ImmutableValueGraph<N, V>)Preconditions.checkNotNull(graph);
  }
  
  public ElementOrder<N> incidentEdgeOrder() {
    return ElementOrder.stable();
  }
  
  public ImmutableGraph<N> asGraph() {
    return new ImmutableGraph<>(this);
  }
  
  private static <N, V> ImmutableMap<N, GraphConnections<N, V>> getNodeConnections(ValueGraph<N, V> graph) {
    ImmutableMap.Builder<N, GraphConnections<N, V>> nodeConnections = ImmutableMap.builder();
    for (N node : graph.nodes())
      nodeConnections.put(node, connectionsOf(graph, node)); 
    return nodeConnections.buildOrThrow();
  }
  
  private static <N, V> GraphConnections<N, V> connectionsOf(ValueGraph<N, V> graph, N node) {
    Function<N, V> successorNodeToValueFn = successorNode -> Objects.requireNonNull(graph.edgeValueOrDefault(node, successorNode, null));
    return graph.isDirected() ? 
      DirectedGraphConnections.<N, V>ofImmutable(node, graph
        .incidentEdges(node), successorNodeToValueFn) : 
      UndirectedGraphConnections.<N, V>ofImmutable(
        Maps.asMap(graph.adjacentNodes(node), successorNodeToValueFn));
  }
  
  public static class Builder<N, V> {
    private final MutableValueGraph<N, V> mutableValueGraph;
    
    Builder(ValueGraphBuilder<N, V> graphBuilder) {
      this
        .mutableValueGraph = graphBuilder.copy().incidentEdgeOrder(ElementOrder.stable()).build();
    }
    
    @CanIgnoreReturnValue
    public Builder<N, V> addNode(N node) {
      this.mutableValueGraph.addNode(node);
      return this;
    }
    
    @CanIgnoreReturnValue
    public Builder<N, V> putEdgeValue(N nodeU, N nodeV, V value) {
      this.mutableValueGraph.putEdgeValue(nodeU, nodeV, value);
      return this;
    }
    
    @CanIgnoreReturnValue
    public Builder<N, V> putEdgeValue(EndpointPair<N> endpoints, V value) {
      this.mutableValueGraph.putEdgeValue(endpoints, value);
      return this;
    }
    
    public ImmutableValueGraph<N, V> build() {
      return ImmutableValueGraph.copyOf(this.mutableValueGraph);
    }
  }
}
