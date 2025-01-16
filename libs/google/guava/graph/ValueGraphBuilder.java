package me.syncwrld.booter.libs.google.guava.graph;

import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.Beta;
import me.syncwrld.booter.libs.google.guava.base.Optional;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;

@ElementTypesAreNonnullByDefault
@Beta
public final class ValueGraphBuilder<N, V> extends AbstractGraphBuilder<N> {
  private ValueGraphBuilder(boolean directed) {
    super(directed);
  }
  
  public static ValueGraphBuilder<Object, Object> directed() {
    return new ValueGraphBuilder<>(true);
  }
  
  public static ValueGraphBuilder<Object, Object> undirected() {
    return new ValueGraphBuilder<>(false);
  }
  
  public static <N, V> ValueGraphBuilder<N, V> from(ValueGraph<N, V> graph) {
    return (new ValueGraphBuilder<>(graph.isDirected()))
      .allowsSelfLoops(graph.allowsSelfLoops())
      .nodeOrder(graph.nodeOrder())
      .incidentEdgeOrder(graph.incidentEdgeOrder());
  }
  
  public <N1 extends N, V1 extends V> ImmutableValueGraph.Builder<N1, V1> immutable() {
    ValueGraphBuilder<N1, V1> castBuilder = cast();
    return new ImmutableValueGraph.Builder<>(castBuilder);
  }
  
  @CanIgnoreReturnValue
  public ValueGraphBuilder<N, V> allowsSelfLoops(boolean allowsSelfLoops) {
    this.allowsSelfLoops = allowsSelfLoops;
    return this;
  }
  
  @CanIgnoreReturnValue
  public ValueGraphBuilder<N, V> expectedNodeCount(int expectedNodeCount) {
    this.expectedNodeCount = Optional.of(Integer.valueOf(Graphs.checkNonNegative(expectedNodeCount)));
    return this;
  }
  
  public <N1 extends N> ValueGraphBuilder<N1, V> nodeOrder(ElementOrder<N1> nodeOrder) {
    ValueGraphBuilder<N1, V> newBuilder = cast();
    newBuilder.nodeOrder = (ElementOrder<N>)Preconditions.checkNotNull(nodeOrder);
    return newBuilder;
  }
  
  public <N1 extends N> ValueGraphBuilder<N1, V> incidentEdgeOrder(ElementOrder<N1> incidentEdgeOrder) {
    Preconditions.checkArgument((incidentEdgeOrder
        .type() == ElementOrder.Type.UNORDERED || incidentEdgeOrder
        .type() == ElementOrder.Type.STABLE), "The given elementOrder (%s) is unsupported. incidentEdgeOrder() only supports ElementOrder.unordered() and ElementOrder.stable().", incidentEdgeOrder);
    ValueGraphBuilder<N1, V> newBuilder = cast();
    newBuilder.incidentEdgeOrder = (ElementOrder<N>)Preconditions.checkNotNull(incidentEdgeOrder);
    return newBuilder;
  }
  
  public <N1 extends N, V1 extends V> MutableValueGraph<N1, V1> build() {
    return new StandardMutableValueGraph<>(this);
  }
  
  ValueGraphBuilder<N, V> copy() {
    ValueGraphBuilder<N, V> newBuilder = new ValueGraphBuilder(this.directed);
    newBuilder.allowsSelfLoops = this.allowsSelfLoops;
    newBuilder.nodeOrder = this.nodeOrder;
    newBuilder.expectedNodeCount = this.expectedNodeCount;
    newBuilder.incidentEdgeOrder = this.incidentEdgeOrder;
    return newBuilder;
  }
  
  private <N1 extends N, V1 extends V> ValueGraphBuilder<N1, V1> cast() {
    return this;
  }
}
