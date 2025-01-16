package me.syncwrld.booter.libs.google.guava.graph;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.guava.collect.ImmutableSet;

@ElementTypesAreNonnullByDefault
class StandardNetwork<N, E> extends AbstractNetwork<N, E> {
  private final boolean isDirected;
  
  private final boolean allowsParallelEdges;
  
  private final boolean allowsSelfLoops;
  
  private final ElementOrder<N> nodeOrder;
  
  private final ElementOrder<E> edgeOrder;
  
  final MapIteratorCache<N, NetworkConnections<N, E>> nodeConnections;
  
  final MapIteratorCache<E, N> edgeToReferenceNode;
  
  StandardNetwork(NetworkBuilder<? super N, ? super E> builder) {
    this(builder, builder.nodeOrder
        
        .createMap(((Integer)builder.expectedNodeCount
          .or(Integer.valueOf(10))).intValue()), builder.edgeOrder
        .createMap(((Integer)builder.expectedEdgeCount.or(Integer.valueOf(20))).intValue()));
  }
  
  StandardNetwork(NetworkBuilder<? super N, ? super E> builder, Map<N, NetworkConnections<N, E>> nodeConnections, Map<E, N> edgeToReferenceNode) {
    this.isDirected = builder.directed;
    this.allowsParallelEdges = builder.allowsParallelEdges;
    this.allowsSelfLoops = builder.allowsSelfLoops;
    this.nodeOrder = builder.nodeOrder.cast();
    this.edgeOrder = builder.edgeOrder.cast();
    this
      
      .nodeConnections = (nodeConnections instanceof java.util.TreeMap) ? new MapRetrievalCache<>(nodeConnections) : new MapIteratorCache<>(nodeConnections);
    this.edgeToReferenceNode = new MapIteratorCache<>(edgeToReferenceNode);
  }
  
  public Set<N> nodes() {
    return this.nodeConnections.unmodifiableKeySet();
  }
  
  public Set<E> edges() {
    return this.edgeToReferenceNode.unmodifiableKeySet();
  }
  
  public boolean isDirected() {
    return this.isDirected;
  }
  
  public boolean allowsParallelEdges() {
    return this.allowsParallelEdges;
  }
  
  public boolean allowsSelfLoops() {
    return this.allowsSelfLoops;
  }
  
  public ElementOrder<N> nodeOrder() {
    return this.nodeOrder;
  }
  
  public ElementOrder<E> edgeOrder() {
    return this.edgeOrder;
  }
  
  public Set<E> incidentEdges(N node) {
    return checkedConnections(node).incidentEdges();
  }
  
  public EndpointPair<N> incidentNodes(E edge) {
    N nodeU = checkedReferenceNode(edge);
    N nodeV = ((NetworkConnections<N, E>)Objects.<NetworkConnections<N, E>>requireNonNull(this.nodeConnections.get(nodeU))).adjacentNode(edge);
    return EndpointPair.of(this, nodeU, nodeV);
  }
  
  public Set<N> adjacentNodes(N node) {
    return checkedConnections(node).adjacentNodes();
  }
  
  public Set<E> edgesConnecting(N nodeU, N nodeV) {
    NetworkConnections<N, E> connectionsU = checkedConnections(nodeU);
    if (!this.allowsSelfLoops && nodeU == nodeV)
      return (Set<E>)ImmutableSet.of(); 
    Preconditions.checkArgument(containsNode(nodeV), "Node %s is not an element of this graph.", nodeV);
    return connectionsU.edgesConnecting(nodeV);
  }
  
  public Set<E> inEdges(N node) {
    return checkedConnections(node).inEdges();
  }
  
  public Set<E> outEdges(N node) {
    return checkedConnections(node).outEdges();
  }
  
  public Set<N> predecessors(N node) {
    return checkedConnections(node).predecessors();
  }
  
  public Set<N> successors(N node) {
    return checkedConnections(node).successors();
  }
  
  final NetworkConnections<N, E> checkedConnections(N node) {
    NetworkConnections<N, E> connections = this.nodeConnections.get(node);
    if (connections == null) {
      Preconditions.checkNotNull(node);
      throw new IllegalArgumentException(String.format("Node %s is not an element of this graph.", new Object[] { node }));
    } 
    return connections;
  }
  
  final N checkedReferenceNode(E edge) {
    N referenceNode = this.edgeToReferenceNode.get(edge);
    if (referenceNode == null) {
      Preconditions.checkNotNull(edge);
      throw new IllegalArgumentException(String.format("Edge %s is not an element of this graph.", new Object[] { edge }));
    } 
    return referenceNode;
  }
  
  final boolean containsNode(N node) {
    return this.nodeConnections.containsKey(node);
  }
  
  final boolean containsEdge(E edge) {
    return this.edgeToReferenceNode.containsKey(edge);
  }
}
