package me.syncwrld.booter.libs.google.guava.graph;

import java.util.Objects;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.guava.collect.ImmutableList;
import me.syncwrld.booter.libs.google.guava.collect.UnmodifiableIterator;

@ElementTypesAreNonnullByDefault
final class StandardMutableNetwork<N, E> extends StandardNetwork<N, E> implements MutableNetwork<N, E> {
  StandardMutableNetwork(NetworkBuilder<? super N, ? super E> builder) {
    super(builder);
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
  private NetworkConnections<N, E> addNodeInternal(N node) {
    NetworkConnections<N, E> connections = newConnections();
    Preconditions.checkState((this.nodeConnections.put(node, connections) == null));
    return connections;
  }
  
  @CanIgnoreReturnValue
  public boolean addEdge(N nodeU, N nodeV, E edge) {
    Preconditions.checkNotNull(nodeU, "nodeU");
    Preconditions.checkNotNull(nodeV, "nodeV");
    Preconditions.checkNotNull(edge, "edge");
    if (containsEdge(edge)) {
      EndpointPair<N> existingIncidentNodes = incidentNodes(edge);
      EndpointPair<N> newIncidentNodes = EndpointPair.of(this, nodeU, nodeV);
      Preconditions.checkArgument(existingIncidentNodes
          .equals(newIncidentNodes), "Edge %s already exists between the following nodes: %s, so it cannot be reused to connect the following nodes: %s.", edge, existingIncidentNodes, newIncidentNodes);
      return false;
    } 
    NetworkConnections<N, E> connectionsU = this.nodeConnections.get(nodeU);
    if (!allowsParallelEdges())
      Preconditions.checkArgument((connectionsU == null || 
          !connectionsU.successors().contains(nodeV)), "Nodes %s and %s are already connected by a different edge. To construct a graph that allows parallel edges, call allowsParallelEdges(true) on the Builder.", nodeU, nodeV); 
    boolean isSelfLoop = nodeU.equals(nodeV);
    if (!allowsSelfLoops())
      Preconditions.checkArgument(!isSelfLoop, "Cannot add self-loop edge on node %s, as self-loops are not allowed. To construct a graph that allows self-loops, call allowsSelfLoops(true) on the Builder.", nodeU); 
    if (connectionsU == null)
      connectionsU = addNodeInternal(nodeU); 
    connectionsU.addOutEdge(edge, nodeV);
    NetworkConnections<N, E> connectionsV = this.nodeConnections.get(nodeV);
    if (connectionsV == null)
      connectionsV = addNodeInternal(nodeV); 
    connectionsV.addInEdge(edge, nodeU, isSelfLoop);
    this.edgeToReferenceNode.put(edge, nodeU);
    return true;
  }
  
  @CanIgnoreReturnValue
  public boolean addEdge(EndpointPair<N> endpoints, E edge) {
    validateEndpoints(endpoints);
    return addEdge(endpoints.nodeU(), endpoints.nodeV(), edge);
  }
  
  @CanIgnoreReturnValue
  public boolean removeNode(N node) {
    Preconditions.checkNotNull(node, "node");
    NetworkConnections<N, E> connections = this.nodeConnections.get(node);
    if (connections == null)
      return false; 
    for (UnmodifiableIterator<E> unmodifiableIterator = ImmutableList.copyOf(connections.incidentEdges()).iterator(); unmodifiableIterator.hasNext(); ) {
      E edge = unmodifiableIterator.next();
      removeEdge(edge);
    } 
    this.nodeConnections.remove(node);
    return true;
  }
  
  @CanIgnoreReturnValue
  public boolean removeEdge(E edge) {
    Preconditions.checkNotNull(edge, "edge");
    N nodeU = this.edgeToReferenceNode.get(edge);
    if (nodeU == null)
      return false; 
    NetworkConnections<N, E> connectionsU = Objects.<NetworkConnections<N, E>>requireNonNull(this.nodeConnections.get(nodeU));
    N nodeV = connectionsU.adjacentNode(edge);
    NetworkConnections<N, E> connectionsV = Objects.<NetworkConnections<N, E>>requireNonNull(this.nodeConnections.get(nodeV));
    connectionsU.removeOutEdge(edge);
    connectionsV.removeInEdge(edge, (allowsSelfLoops() && nodeU.equals(nodeV)));
    this.edgeToReferenceNode.remove(edge);
    return true;
  }
  
  private NetworkConnections<N, E> newConnections() {
    return isDirected() ? (
      allowsParallelEdges() ? 
      DirectedMultiNetworkConnections.<N, E>of() : 
      DirectedNetworkConnections.<N, E>of()) : (
      allowsParallelEdges() ? 
      UndirectedMultiNetworkConnections.<N, E>of() : 
      UndirectedNetworkConnections.<N, E>of());
  }
}
