package me.syncwrld.booter.libs.google.guava.graph;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.guava.collect.ImmutableSet;
import me.syncwrld.booter.libs.google.guava.collect.Iterators;
import me.syncwrld.booter.libs.google.guava.collect.Sets;
import me.syncwrld.booter.libs.google.guava.collect.UnmodifiableIterator;
import me.syncwrld.booter.libs.google.guava.math.IntMath;
import me.syncwrld.booter.libs.google.guava.primitives.Ints;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
abstract class AbstractBaseGraph<N> implements BaseGraph<N> {
  protected long edgeCount() {
    long degreeSum = 0L;
    for (N node : nodes())
      degreeSum += degree(node); 
    Preconditions.checkState(((degreeSum & 0x1L) == 0L));
    return degreeSum >>> 1L;
  }
  
  public Set<EndpointPair<N>> edges() {
    return new AbstractSet<EndpointPair<N>>() {
        public UnmodifiableIterator<EndpointPair<N>> iterator() {
          return (UnmodifiableIterator)EndpointPairIterator.of(AbstractBaseGraph.this);
        }
        
        public int size() {
          return Ints.saturatedCast(AbstractBaseGraph.this.edgeCount());
        }
        
        public boolean remove(@CheckForNull Object o) {
          throw new UnsupportedOperationException();
        }
        
        public boolean contains(@CheckForNull Object obj) {
          if (!(obj instanceof EndpointPair))
            return false; 
          EndpointPair<?> endpointPair = (EndpointPair)obj;
          return (AbstractBaseGraph.this.isOrderingCompatible(endpointPair) && AbstractBaseGraph.this
            .nodes().contains(endpointPair.nodeU()) && AbstractBaseGraph.this
            .successors(endpointPair.nodeU()).contains(endpointPair.nodeV()));
        }
      };
  }
  
  public ElementOrder<N> incidentEdgeOrder() {
    return ElementOrder.unordered();
  }
  
  public Set<EndpointPair<N>> incidentEdges(N node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(nodes().contains(node), "Node %s is not an element of this graph.", node);
    return new IncidentEdgeSet<N>(this, this, node) {
        public UnmodifiableIterator<EndpointPair<N>> iterator() {
          if (this.graph.isDirected())
            return Iterators.unmodifiableIterator(
                Iterators.concat(
                  Iterators.transform(this.graph
                    .predecessors(this.node).iterator(), predecessor -> EndpointPair.ordered(predecessor, this.node)), 
                  
                  Iterators.transform(
                    
                    (Iterator)Sets.difference(this.graph.successors(this.node), (Set)ImmutableSet.of(this.node)).iterator(), successor -> EndpointPair.ordered(this.node, (N)successor)))); 
          return Iterators.unmodifiableIterator(
              Iterators.transform(this.graph
                .adjacentNodes(this.node).iterator(), adjacentNode -> EndpointPair.unordered(this.node, (N)adjacentNode)));
        }
      };
  }
  
  public int degree(N node) {
    if (isDirected())
      return IntMath.saturatedAdd(predecessors(node).size(), successors(node).size()); 
    Set<N> neighbors = adjacentNodes(node);
    int selfLoopCount = (allowsSelfLoops() && neighbors.contains(node)) ? 1 : 0;
    return IntMath.saturatedAdd(neighbors.size(), selfLoopCount);
  }
  
  public int inDegree(N node) {
    return isDirected() ? predecessors(node).size() : degree(node);
  }
  
  public int outDegree(N node) {
    return isDirected() ? successors(node).size() : degree(node);
  }
  
  public boolean hasEdgeConnecting(N nodeU, N nodeV) {
    Preconditions.checkNotNull(nodeU);
    Preconditions.checkNotNull(nodeV);
    return (nodes().contains(nodeU) && successors(nodeU).contains(nodeV));
  }
  
  public boolean hasEdgeConnecting(EndpointPair<N> endpoints) {
    Preconditions.checkNotNull(endpoints);
    if (!isOrderingCompatible(endpoints))
      return false; 
    N nodeU = endpoints.nodeU();
    N nodeV = endpoints.nodeV();
    return (nodes().contains(nodeU) && successors(nodeU).contains(nodeV));
  }
  
  protected final void validateEndpoints(EndpointPair<?> endpoints) {
    Preconditions.checkNotNull(endpoints);
    Preconditions.checkArgument(isOrderingCompatible(endpoints), "Mismatch: endpoints' ordering is not compatible with directionality of the graph");
  }
  
  protected final boolean isOrderingCompatible(EndpointPair<?> endpoints) {
    return (endpoints.isOrdered() == isDirected());
  }
}
