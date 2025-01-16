package me.syncwrld.booter.libs.google.guava.graph;

import java.util.Objects;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.guava.collect.ImmutableList;
import me.syncwrld.booter.libs.google.guava.collect.UnmodifiableIterator;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
final class StandardMutableValueGraph<N, V> extends StandardValueGraph<N, V> implements MutableValueGraph<N, V> {
  private final ElementOrder<N> incidentEdgeOrder;
  
  StandardMutableValueGraph(AbstractGraphBuilder<? super N> builder) {
    super(builder);
    this.incidentEdgeOrder = builder.incidentEdgeOrder.cast();
  }
  
  public ElementOrder<N> incidentEdgeOrder() {
    return this.incidentEdgeOrder;
  }
  
  @CanIgnoreReturnValue
  public boolean addNode(N node) {
    Preconditions.checkNotNull(node, "node");
    if (containsNode(node))
      return false; 
    addNodeInternal(node);
    return true;
  }
  
  @CanIgnoreReturnValue
  private GraphConnections<N, V> addNodeInternal(N node) {
    GraphConnections<N, V> connections = newConnections();
    Preconditions.checkState((this.nodeConnections.put(node, connections) == null));
    return connections;
  }
  
  @CheckForNull
  @CanIgnoreReturnValue
  public V putEdgeValue(N nodeU, N nodeV, V value) {
    Preconditions.checkNotNull(nodeU, "nodeU");
    Preconditions.checkNotNull(nodeV, "nodeV");
    Preconditions.checkNotNull(value, "value");
    if (!allowsSelfLoops())
      Preconditions.checkArgument(!nodeU.equals(nodeV), "Cannot add self-loop edge on node %s, as self-loops are not allowed. To construct a graph that allows self-loops, call allowsSelfLoops(true) on the Builder.", nodeU); 
    GraphConnections<N, V> connectionsU = this.nodeConnections.get(nodeU);
    if (connectionsU == null)
      connectionsU = addNodeInternal(nodeU); 
    V previousValue = connectionsU.addSuccessor(nodeV, value);
    GraphConnections<N, V> connectionsV = this.nodeConnections.get(nodeV);
    if (connectionsV == null)
      connectionsV = addNodeInternal(nodeV); 
    connectionsV.addPredecessor(nodeU, value);
    if (previousValue == null)
      Graphs.checkPositive(++this.edgeCount); 
    return previousValue;
  }
  
  @CheckForNull
  @CanIgnoreReturnValue
  public V putEdgeValue(EndpointPair<N> endpoints, V value) {
    validateEndpoints(endpoints);
    return putEdgeValue(endpoints.nodeU(), endpoints.nodeV(), value);
  }
  
  @CanIgnoreReturnValue
  public boolean removeNode(N node) {
    Preconditions.checkNotNull(node, "node");
    GraphConnections<N, V> connections = this.nodeConnections.get(node);
    if (connections == null)
      return false; 
    if (allowsSelfLoops())
      if (connections.removeSuccessor(node) != null) {
        connections.removePredecessor(node);
        this.edgeCount--;
      }  
    UnmodifiableIterator<N> unmodifiableIterator;
    for (unmodifiableIterator = ImmutableList.copyOf(connections.successors()).iterator(); unmodifiableIterator.hasNext(); ) {
      N successor = unmodifiableIterator.next();
      ((GraphConnections)Objects.<GraphConnections>requireNonNull(this.nodeConnections.getWithoutCaching(successor))).removePredecessor(node);
      Objects.requireNonNull(connections.removeSuccessor(successor));
      this.edgeCount--;
    } 
    if (isDirected())
      for (unmodifiableIterator = ImmutableList.copyOf(connections.predecessors()).iterator(); unmodifiableIterator.hasNext(); ) {
        N predecessor = unmodifiableIterator.next();
        Preconditions.checkState(
            (((GraphConnections)Objects.<GraphConnections>requireNonNull(this.nodeConnections.getWithoutCaching(predecessor))).removeSuccessor(node) != null));
        connections.removePredecessor(predecessor);
        this.edgeCount--;
      }  
    this.nodeConnections.remove(node);
    Graphs.checkNonNegative(this.edgeCount);
    return true;
  }
  
  @CheckForNull
  @CanIgnoreReturnValue
  public V removeEdge(N nodeU, N nodeV) {
    Preconditions.checkNotNull(nodeU, "nodeU");
    Preconditions.checkNotNull(nodeV, "nodeV");
    GraphConnections<N, V> connectionsU = this.nodeConnections.get(nodeU);
    GraphConnections<N, V> connectionsV = this.nodeConnections.get(nodeV);
    if (connectionsU == null || connectionsV == null)
      return null; 
    V previousValue = connectionsU.removeSuccessor(nodeV);
    if (previousValue != null) {
      connectionsV.removePredecessor(nodeU);
      Graphs.checkNonNegative(--this.edgeCount);
    } 
    return previousValue;
  }
  
  @CheckForNull
  @CanIgnoreReturnValue
  public V removeEdge(EndpointPair<N> endpoints) {
    validateEndpoints(endpoints);
    return removeEdge(endpoints.nodeU(), endpoints.nodeV());
  }
  
  private GraphConnections<N, V> newConnections() {
    return isDirected() ? 
      DirectedGraphConnections.<N, V>of(this.incidentEdgeOrder) : 
      UndirectedGraphConnections.<N, V>of(this.incidentEdgeOrder);
  }
}
