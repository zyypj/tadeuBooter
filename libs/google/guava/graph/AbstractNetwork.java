package me.syncwrld.booter.libs.google.guava.graph;

import java.util.AbstractSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import me.syncwrld.booter.libs.google.guava.annotations.Beta;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.guava.base.Predicate;
import me.syncwrld.booter.libs.google.guava.collect.ImmutableSet;
import me.syncwrld.booter.libs.google.guava.collect.Iterators;
import me.syncwrld.booter.libs.google.guava.collect.Maps;
import me.syncwrld.booter.libs.google.guava.collect.Sets;
import me.syncwrld.booter.libs.google.guava.math.IntMath;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@Beta
public abstract class AbstractNetwork<N, E> implements Network<N, E> {
  public Graph<N> asGraph() {
    return new AbstractGraph<N>() {
        public Set<N> nodes() {
          return AbstractNetwork.this.nodes();
        }
        
        public Set<EndpointPair<N>> edges() {
          if (AbstractNetwork.this.allowsParallelEdges())
            return super.edges(); 
          return new AbstractSet<EndpointPair<N>>() {
              public Iterator<EndpointPair<N>> iterator() {
                return Iterators.transform(AbstractNetwork.this
                    .edges().iterator(), edge -> AbstractNetwork.this.incidentNodes(edge));
              }
              
              public int size() {
                return AbstractNetwork.this.edges().size();
              }
              
              public boolean contains(@CheckForNull Object obj) {
                if (!(obj instanceof EndpointPair))
                  return false; 
                EndpointPair<?> endpointPair = (EndpointPair)obj;
                return (AbstractNetwork.null.this.isOrderingCompatible(endpointPair) && AbstractNetwork.null.this
                  .nodes().contains(endpointPair.nodeU()) && AbstractNetwork.null.this
                  .successors((N)endpointPair.nodeU()).contains(endpointPair.nodeV()));
              }
            };
        }
        
        public ElementOrder<N> nodeOrder() {
          return AbstractNetwork.this.nodeOrder();
        }
        
        public ElementOrder<N> incidentEdgeOrder() {
          return ElementOrder.unordered();
        }
        
        public boolean isDirected() {
          return AbstractNetwork.this.isDirected();
        }
        
        public boolean allowsSelfLoops() {
          return AbstractNetwork.this.allowsSelfLoops();
        }
        
        public Set<N> adjacentNodes(N node) {
          return AbstractNetwork.this.adjacentNodes(node);
        }
        
        public Set<N> predecessors(N node) {
          return AbstractNetwork.this.predecessors(node);
        }
        
        public Set<N> successors(N node) {
          return AbstractNetwork.this.successors(node);
        }
      };
  }
  
  public int degree(N node) {
    if (isDirected())
      return IntMath.saturatedAdd(inEdges(node).size(), outEdges(node).size()); 
    return IntMath.saturatedAdd(incidentEdges(node).size(), edgesConnecting(node, node).size());
  }
  
  public int inDegree(N node) {
    return isDirected() ? inEdges(node).size() : degree(node);
  }
  
  public int outDegree(N node) {
    return isDirected() ? outEdges(node).size() : degree(node);
  }
  
  public Set<E> adjacentEdges(E edge) {
    EndpointPair<N> endpointPair = incidentNodes(edge);
    Sets.SetView setView = Sets.union(incidentEdges(endpointPair.nodeU()), incidentEdges(endpointPair.nodeV()));
    return (Set<E>)Sets.difference((Set)setView, (Set)ImmutableSet.of(edge));
  }
  
  public Set<E> edgesConnecting(N nodeU, N nodeV) {
    Set<E> outEdgesU = outEdges(nodeU);
    Set<E> inEdgesV = inEdges(nodeV);
    return (outEdgesU.size() <= inEdgesV.size()) ? 
      Collections.<E>unmodifiableSet(Sets.filter(outEdgesU, connectedPredicate(nodeU, nodeV))) : 
      Collections.<E>unmodifiableSet(Sets.filter(inEdgesV, connectedPredicate(nodeV, nodeU)));
  }
  
  public Set<E> edgesConnecting(EndpointPair<N> endpoints) {
    validateEndpoints(endpoints);
    return edgesConnecting(endpoints.nodeU(), endpoints.nodeV());
  }
  
  private Predicate<E> connectedPredicate(final N nodePresent, final N nodeToCheck) {
    return new Predicate<E>() {
        public boolean apply(E edge) {
          return AbstractNetwork.this.incidentNodes(edge).adjacentNode((N)nodePresent).equals(nodeToCheck);
        }
      };
  }
  
  public Optional<E> edgeConnecting(N nodeU, N nodeV) {
    return Optional.ofNullable(edgeConnectingOrNull(nodeU, nodeV));
  }
  
  public Optional<E> edgeConnecting(EndpointPair<N> endpoints) {
    validateEndpoints(endpoints);
    return edgeConnecting(endpoints.nodeU(), endpoints.nodeV());
  }
  
  @CheckForNull
  public E edgeConnectingOrNull(N nodeU, N nodeV) {
    Set<E> edgesConnecting = edgesConnecting(nodeU, nodeV);
    switch (edgesConnecting.size()) {
      case 0:
        return null;
      case 1:
        return edgesConnecting.iterator().next();
    } 
    throw new IllegalArgumentException(String.format("Cannot call edgeConnecting() when parallel edges exist between %s and %s. Consider calling edgesConnecting() instead.", new Object[] { nodeU, nodeV }));
  }
  
  @CheckForNull
  public E edgeConnectingOrNull(EndpointPair<N> endpoints) {
    validateEndpoints(endpoints);
    return edgeConnectingOrNull(endpoints.nodeU(), endpoints.nodeV());
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
    return hasEdgeConnecting(endpoints.nodeU(), endpoints.nodeV());
  }
  
  protected final void validateEndpoints(EndpointPair<?> endpoints) {
    Preconditions.checkNotNull(endpoints);
    Preconditions.checkArgument(isOrderingCompatible(endpoints), "Mismatch: endpoints' ordering is not compatible with directionality of the graph");
  }
  
  protected final boolean isOrderingCompatible(EndpointPair<?> endpoints) {
    return (endpoints.isOrdered() == isDirected());
  }
  
  public final boolean equals(@CheckForNull Object obj) {
    if (obj == this)
      return true; 
    if (!(obj instanceof Network))
      return false; 
    Network<?, ?> other = (Network<?, ?>)obj;
    return (isDirected() == other.isDirected() && 
      nodes().equals(other.nodes()) && 
      edgeIncidentNodesMap(this).equals(edgeIncidentNodesMap(other)));
  }
  
  public final int hashCode() {
    return edgeIncidentNodesMap(this).hashCode();
  }
  
  public String toString() {
    return "isDirected: " + 
      isDirected() + ", allowsParallelEdges: " + 
      
      allowsParallelEdges() + ", allowsSelfLoops: " + 
      
      allowsSelfLoops() + ", nodes: " + 
      
      nodes() + ", edges: " + 
      
      edgeIncidentNodesMap(this);
  }
  
  private static <N, E> Map<E, EndpointPair<N>> edgeIncidentNodesMap(Network<N, E> network) {
    Objects.requireNonNull(network);
    return Maps.asMap(network.edges(), network::incidentNodes);
  }
}
