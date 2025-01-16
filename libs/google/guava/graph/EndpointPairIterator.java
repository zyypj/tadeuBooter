package me.syncwrld.booter.libs.google.guava.graph;

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.guava.collect.AbstractIterator;
import me.syncwrld.booter.libs.google.guava.collect.ImmutableSet;
import me.syncwrld.booter.libs.google.guava.collect.Sets;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
abstract class EndpointPairIterator<N> extends AbstractIterator<EndpointPair<N>> {
  private final BaseGraph<N> graph;
  
  private final Iterator<N> nodeIterator;
  
  @CheckForNull
  N node = null;
  
  Iterator<N> successorIterator = (Iterator<N>)ImmutableSet.of().iterator();
  
  static <N> EndpointPairIterator<N> of(BaseGraph<N> graph) {
    return graph.isDirected() ? new Directed<>(graph) : new Undirected<>(graph);
  }
  
  private EndpointPairIterator(BaseGraph<N> graph) {
    this.graph = graph;
    this.nodeIterator = graph.nodes().iterator();
  }
  
  final boolean advance() {
    Preconditions.checkState(!this.successorIterator.hasNext());
    if (!this.nodeIterator.hasNext())
      return false; 
    this.node = this.nodeIterator.next();
    this.successorIterator = this.graph.successors(this.node).iterator();
    return true;
  }
  
  private static final class Directed<N> extends EndpointPairIterator<N> {
    private Directed(BaseGraph<N> graph) {
      super(graph);
    }
    
    @CheckForNull
    protected EndpointPair<N> computeNext() {
      while (true) {
        if (this.successorIterator.hasNext())
          return EndpointPair.ordered(Objects.requireNonNull(this.node), this.successorIterator.next()); 
        if (!advance())
          return (EndpointPair<N>)endOfData(); 
      } 
    }
  }
  
  private static final class Undirected<N> extends EndpointPairIterator<N> {
    @CheckForNull
    private Set<N> visitedNodes;
    
    private Undirected(BaseGraph<N> graph) {
      super(graph);
      this.visitedNodes = Sets.newHashSetWithExpectedSize(graph.nodes().size() + 1);
    }
    
    @CheckForNull
    protected EndpointPair<N> computeNext() {
      while (true) {
        Objects.requireNonNull(this.visitedNodes);
        while (this.successorIterator.hasNext()) {
          N otherNode = this.successorIterator.next();
          if (!this.visitedNodes.contains(otherNode))
            return EndpointPair.unordered(Objects.requireNonNull(this.node), otherNode); 
        } 
        this.visitedNodes.add(this.node);
        if (!advance()) {
          this.visitedNodes = null;
          return (EndpointPair<N>)endOfData();
        } 
      } 
    }
  }
}
