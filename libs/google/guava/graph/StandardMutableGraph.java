package me.syncwrld.booter.libs.google.guava.graph;

@ElementTypesAreNonnullByDefault
final class StandardMutableGraph<N> extends ForwardingGraph<N> implements MutableGraph<N> {
  private final MutableValueGraph<N, GraphConstants.Presence> backingValueGraph;
  
  StandardMutableGraph(AbstractGraphBuilder<? super N> builder) {
    this.backingValueGraph = new StandardMutableValueGraph<>(builder);
  }
  
  BaseGraph<N> delegate() {
    return this.backingValueGraph;
  }
  
  public boolean addNode(N node) {
    return this.backingValueGraph.addNode(node);
  }
  
  public boolean putEdge(N nodeU, N nodeV) {
    return (this.backingValueGraph.putEdgeValue(nodeU, nodeV, GraphConstants.Presence.EDGE_EXISTS) == null);
  }
  
  public boolean putEdge(EndpointPair<N> endpoints) {
    validateEndpoints(endpoints);
    return putEdge(endpoints.nodeU(), endpoints.nodeV());
  }
  
  public boolean removeNode(N node) {
    return this.backingValueGraph.removeNode(node);
  }
  
  public boolean removeEdge(N nodeU, N nodeV) {
    return (this.backingValueGraph.removeEdge(nodeU, nodeV) != null);
  }
  
  public boolean removeEdge(EndpointPair<N> endpoints) {
    validateEndpoints(endpoints);
    return removeEdge(endpoints.nodeU(), endpoints.nodeV());
  }
}
