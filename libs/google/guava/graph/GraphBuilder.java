package me.syncwrld.booter.libs.google.guava.graph;

import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.errorprone.annotations.DoNotMock;
import me.syncwrld.booter.libs.google.guava.annotations.Beta;
import me.syncwrld.booter.libs.google.guava.base.Optional;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;

@DoNotMock
@ElementTypesAreNonnullByDefault
@Beta
public final class GraphBuilder<N> extends AbstractGraphBuilder<N> {
  private GraphBuilder(boolean directed) {
    super(directed);
  }
  
  public static GraphBuilder<Object> directed() {
    return new GraphBuilder(true);
  }
  
  public static GraphBuilder<Object> undirected() {
    return new GraphBuilder(false);
  }
  
  public static <N> GraphBuilder<N> from(Graph<N> graph) {
    return (new GraphBuilder(graph.isDirected()))
      .allowsSelfLoops(graph.allowsSelfLoops())
      .nodeOrder(graph.nodeOrder())
      .incidentEdgeOrder(graph.incidentEdgeOrder());
  }
  
  public <N1 extends N> ImmutableGraph.Builder<N1> immutable() {
    GraphBuilder<N1> castBuilder = cast();
    return new ImmutableGraph.Builder<>(castBuilder);
  }
  
  @CanIgnoreReturnValue
  public GraphBuilder<N> allowsSelfLoops(boolean allowsSelfLoops) {
    this.allowsSelfLoops = allowsSelfLoops;
    return this;
  }
  
  @CanIgnoreReturnValue
  public GraphBuilder<N> expectedNodeCount(int expectedNodeCount) {
    this.expectedNodeCount = Optional.of(Integer.valueOf(Graphs.checkNonNegative(expectedNodeCount)));
    return this;
  }
  
  public <N1 extends N> GraphBuilder<N1> nodeOrder(ElementOrder<N1> nodeOrder) {
    GraphBuilder<N1> newBuilder = cast();
    newBuilder.nodeOrder = (ElementOrder<N>)Preconditions.checkNotNull(nodeOrder);
    return newBuilder;
  }
  
  public <N1 extends N> GraphBuilder<N1> incidentEdgeOrder(ElementOrder<N1> incidentEdgeOrder) {
    Preconditions.checkArgument((incidentEdgeOrder
        .type() == ElementOrder.Type.UNORDERED || incidentEdgeOrder
        .type() == ElementOrder.Type.STABLE), "The given elementOrder (%s) is unsupported. incidentEdgeOrder() only supports ElementOrder.unordered() and ElementOrder.stable().", incidentEdgeOrder);
    GraphBuilder<N1> newBuilder = cast();
    newBuilder.incidentEdgeOrder = (ElementOrder<N>)Preconditions.checkNotNull(incidentEdgeOrder);
    return newBuilder;
  }
  
  public <N1 extends N> MutableGraph<N1> build() {
    return new StandardMutableGraph<>(this);
  }
  
  GraphBuilder<N> copy() {
    GraphBuilder<N> newBuilder = new GraphBuilder(this.directed);
    newBuilder.allowsSelfLoops = this.allowsSelfLoops;
    newBuilder.nodeOrder = this.nodeOrder;
    newBuilder.expectedNodeCount = this.expectedNodeCount;
    newBuilder.incidentEdgeOrder = this.incidentEdgeOrder;
    return newBuilder;
  }
  
  private <N1 extends N> GraphBuilder<N1> cast() {
    return this;
  }
}
