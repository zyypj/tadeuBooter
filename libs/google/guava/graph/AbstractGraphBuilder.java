package me.syncwrld.booter.libs.google.guava.graph;

import me.syncwrld.booter.libs.google.guava.base.Optional;

@ElementTypesAreNonnullByDefault
abstract class AbstractGraphBuilder<N> {
  final boolean directed;
  
  boolean allowsSelfLoops = false;
  
  ElementOrder<N> nodeOrder = ElementOrder.insertion();
  
  ElementOrder<N> incidentEdgeOrder = ElementOrder.unordered();
  
  Optional<Integer> expectedNodeCount = Optional.absent();
  
  AbstractGraphBuilder(boolean directed) {
    this.directed = directed;
  }
}
