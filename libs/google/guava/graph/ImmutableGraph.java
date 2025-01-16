package me.syncwrld.booter.libs.google.guava.graph;

import java.util.Map;
import java.util.Set;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.errorprone.annotations.Immutable;
import me.syncwrld.booter.libs.google.guava.annotations.Beta;
import me.syncwrld.booter.libs.google.guava.base.Function;
import me.syncwrld.booter.libs.google.guava.base.Functions;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.guava.collect.ImmutableMap;
import me.syncwrld.booter.libs.google.guava.collect.Maps;

@Immutable(containerOf = {"N"})
@ElementTypesAreNonnullByDefault
@Beta
public class ImmutableGraph<N> extends ForwardingGraph<N> {
  private final BaseGraph<N> backingGraph;
  
  ImmutableGraph(BaseGraph<N> backingGraph) {
    this.backingGraph = backingGraph;
  }
  
  public static <N> ImmutableGraph<N> copyOf(Graph<N> graph) {
    return (graph instanceof ImmutableGraph) ? 
      (ImmutableGraph<N>)graph : 
      new ImmutableGraph<>(new StandardValueGraph<>(
          
          GraphBuilder.from(graph), (Map<N, GraphConnections<N, ?>>)getNodeConnections(graph), graph.edges().size()));
  }
  
  @Deprecated
  public static <N> ImmutableGraph<N> copyOf(ImmutableGraph<N> graph) {
    return (ImmutableGraph<N>)Preconditions.checkNotNull(graph);
  }
  
  public ElementOrder<N> incidentEdgeOrder() {
    return ElementOrder.stable();
  }
  
  private static <N> ImmutableMap<N, GraphConnections<N, GraphConstants.Presence>> getNodeConnections(Graph<N> graph) {
    ImmutableMap.Builder<N, GraphConnections<N, GraphConstants.Presence>> nodeConnections = ImmutableMap.builder();
    for (N node : graph.nodes())
      nodeConnections.put(node, connectionsOf(graph, node)); 
    return nodeConnections.buildOrThrow();
  }
  
  private static <N> GraphConnections<N, GraphConstants.Presence> connectionsOf(Graph<N> graph, N node) {
    Function<N, GraphConstants.Presence> edgeValueFn = Functions.constant(GraphConstants.Presence.EDGE_EXISTS);
    return graph.isDirected() ? 
      DirectedGraphConnections.<N, GraphConstants.Presence>ofImmutable(node, graph.incidentEdges(node), edgeValueFn) : 
      UndirectedGraphConnections.<N, GraphConstants.Presence>ofImmutable(
        Maps.asMap(graph.adjacentNodes(node), edgeValueFn));
  }
  
  BaseGraph<N> delegate() {
    return this.backingGraph;
  }
  
  public static class Builder<N> {
    private final MutableGraph<N> mutableGraph;
    
    Builder(GraphBuilder<N> graphBuilder) {
      this.mutableGraph = graphBuilder.copy().incidentEdgeOrder(ElementOrder.stable()).build();
    }
    
    @CanIgnoreReturnValue
    public Builder<N> addNode(N node) {
      this.mutableGraph.addNode(node);
      return this;
    }
    
    @CanIgnoreReturnValue
    public Builder<N> putEdge(N nodeU, N nodeV) {
      this.mutableGraph.putEdge(nodeU, nodeV);
      return this;
    }
    
    @CanIgnoreReturnValue
    public Builder<N> putEdge(EndpointPair<N> endpoints) {
      this.mutableGraph.putEdge(endpoints);
      return this;
    }
    
    public ImmutableGraph<N> build() {
      return ImmutableGraph.copyOf(this.mutableGraph);
    }
  }
}
