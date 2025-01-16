package me.syncwrld.booter.libs.google.guava.graph;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import me.syncwrld.booter.libs.google.guava.base.Function;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.guava.collect.AbstractIterator;
import me.syncwrld.booter.libs.google.guava.collect.ImmutableList;
import me.syncwrld.booter.libs.google.guava.collect.Iterators;
import me.syncwrld.booter.libs.google.guava.collect.UnmodifiableIterator;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
final class DirectedGraphConnections<N, V> implements GraphConnections<N, V> {
  private static final class PredAndSucc {
    private final Object successorValue;
    
    PredAndSucc(Object successorValue) {
      this.successorValue = successorValue;
    }
  }
  
  private static abstract class NodeConnection<N> {
    final N node;
    
    NodeConnection(N node) {
      this.node = (N)Preconditions.checkNotNull(node);
    }
    
    static final class Pred<N> extends NodeConnection<N> {
      Pred(N node) {
        super(node);
      }
      
      public boolean equals(@CheckForNull Object that) {
        if (that instanceof Pred)
          return this.node.equals(((Pred)that).node); 
        return false;
      }
      
      public int hashCode() {
        return Pred.class.hashCode() + this.node.hashCode();
      }
    }
    
    static final class Succ<N> extends NodeConnection<N> {
      Succ(N node) {
        super(node);
      }
      
      public boolean equals(@CheckForNull Object that) {
        if (that instanceof Succ)
          return this.node.equals(((Succ)that).node); 
        return false;
      }
      
      public int hashCode() {
        return Succ.class.hashCode() + this.node.hashCode();
      }
    }
  }
  
  static final class Pred<N> extends NodeConnection<N> {
    Pred(N node) {
      super(node);
    }
    
    public boolean equals(@CheckForNull Object that) {
      if (that instanceof Pred)
        return this.node.equals(((Pred)that).node); 
      return false;
    }
    
    public int hashCode() {
      return Pred.class.hashCode() + this.node.hashCode();
    }
  }
  
  static final class Succ<N> extends NodeConnection<N> {
    Succ(N node) {
      super(node);
    }
    
    public boolean equals(@CheckForNull Object that) {
      if (that instanceof Succ)
        return this.node.equals(((Succ)that).node); 
      return false;
    }
    
    public int hashCode() {
      return Succ.class.hashCode() + this.node.hashCode();
    }
  }
  
  private static final Object PRED = new Object();
  
  private final Map<N, Object> adjacentNodeValues;
  
  @CheckForNull
  private final List<NodeConnection<N>> orderedNodeConnections;
  
  private int predecessorCount;
  
  private int successorCount;
  
  private DirectedGraphConnections(Map<N, Object> adjacentNodeValues, @CheckForNull List<NodeConnection<N>> orderedNodeConnections, int predecessorCount, int successorCount) {
    this.adjacentNodeValues = (Map<N, Object>)Preconditions.checkNotNull(adjacentNodeValues);
    this.orderedNodeConnections = orderedNodeConnections;
    this.predecessorCount = Graphs.checkNonNegative(predecessorCount);
    this.successorCount = Graphs.checkNonNegative(successorCount);
    Preconditions.checkState((predecessorCount <= adjacentNodeValues
        .size() && successorCount <= adjacentNodeValues
        .size()));
  }
  
  static <N, V> DirectedGraphConnections<N, V> of(ElementOrder<N> incidentEdgeOrder) {
    List<NodeConnection<N>> orderedNodeConnections;
    int initialCapacity = 4;
    switch (incidentEdgeOrder.type()) {
      case UNORDERED:
        orderedNodeConnections = null;
        return new DirectedGraphConnections<>(new HashMap<>(initialCapacity, 1.0F), orderedNodeConnections, 0, 0);
      case STABLE:
        orderedNodeConnections = new ArrayList<>();
        return new DirectedGraphConnections<>(new HashMap<>(initialCapacity, 1.0F), orderedNodeConnections, 0, 0);
    } 
    throw new AssertionError(incidentEdgeOrder.type());
  }
  
  static <N, V> DirectedGraphConnections<N, V> ofImmutable(N thisNode, Iterable<EndpointPair<N>> incidentEdges, Function<N, V> successorNodeToValueFn) {
    Preconditions.checkNotNull(thisNode);
    Preconditions.checkNotNull(successorNodeToValueFn);
    Map<N, Object> adjacentNodeValues = new HashMap<>();
    ImmutableList.Builder<NodeConnection<N>> orderedNodeConnectionsBuilder = ImmutableList.builder();
    int predecessorCount = 0;
    int successorCount = 0;
    for (EndpointPair<N> incidentEdge : incidentEdges) {
      if (incidentEdge.nodeU().equals(thisNode) && incidentEdge.nodeV().equals(thisNode)) {
        adjacentNodeValues.put(thisNode, new PredAndSucc(successorNodeToValueFn.apply(thisNode)));
        orderedNodeConnectionsBuilder.add(new NodeConnection.Pred<>(thisNode));
        orderedNodeConnectionsBuilder.add(new NodeConnection.Succ<>(thisNode));
        predecessorCount++;
        successorCount++;
        continue;
      } 
      if (incidentEdge.nodeV().equals(thisNode)) {
        N predecessor = incidentEdge.nodeU();
        Object object = adjacentNodeValues.put(predecessor, PRED);
        if (object != null)
          adjacentNodeValues.put(predecessor, new PredAndSucc(object)); 
        orderedNodeConnectionsBuilder.add(new NodeConnection.Pred<>(predecessor));
        predecessorCount++;
        continue;
      } 
      Preconditions.checkArgument(incidentEdge.nodeU().equals(thisNode));
      N successor = incidentEdge.nodeV();
      V value = (V)successorNodeToValueFn.apply(successor);
      Object existingValue = adjacentNodeValues.put(successor, value);
      if (existingValue != null) {
        Preconditions.checkArgument((existingValue == PRED));
        adjacentNodeValues.put(successor, new PredAndSucc(value));
      } 
      orderedNodeConnectionsBuilder.add(new NodeConnection.Succ<>(successor));
      successorCount++;
    } 
    return new DirectedGraphConnections<>(adjacentNodeValues, (List<NodeConnection<N>>)orderedNodeConnectionsBuilder
        
        .build(), predecessorCount, successorCount);
  }
  
  public Set<N> adjacentNodes() {
    if (this.orderedNodeConnections == null)
      return Collections.unmodifiableSet(this.adjacentNodeValues.keySet()); 
    return new AbstractSet<N>() {
        public UnmodifiableIterator<N> iterator() {
          final Iterator<DirectedGraphConnections.NodeConnection<N>> nodeConnections = DirectedGraphConnections.this.orderedNodeConnections.iterator();
          final Set<N> seenNodes = new HashSet<>();
          return (UnmodifiableIterator<N>)new AbstractIterator<N>(this) {
              @CheckForNull
              protected N computeNext() {
                while (nodeConnections.hasNext()) {
                  DirectedGraphConnections.NodeConnection<N> nodeConnection = nodeConnections.next();
                  boolean added = seenNodes.add(nodeConnection.node);
                  if (added)
                    return nodeConnection.node; 
                } 
                return (N)endOfData();
              }
            };
        }
        
        public int size() {
          return DirectedGraphConnections.this.adjacentNodeValues.size();
        }
        
        public boolean contains(@CheckForNull Object obj) {
          return DirectedGraphConnections.this.adjacentNodeValues.containsKey(obj);
        }
      };
  }
  
  public Set<N> predecessors() {
    return new AbstractSet<N>() {
        public UnmodifiableIterator<N> iterator() {
          if (DirectedGraphConnections.this.orderedNodeConnections == null) {
            final Iterator<Map.Entry<N, Object>> entries = DirectedGraphConnections.this.adjacentNodeValues.entrySet().iterator();
            return (UnmodifiableIterator<N>)new AbstractIterator<N>(this) {
                @CheckForNull
                protected N computeNext() {
                  while (entries.hasNext()) {
                    Map.Entry<N, Object> entry = entries.next();
                    if (DirectedGraphConnections.isPredecessor(entry.getValue()))
                      return entry.getKey(); 
                  } 
                  return (N)endOfData();
                }
              };
          } 
          final Iterator<DirectedGraphConnections.NodeConnection<N>> nodeConnections = DirectedGraphConnections.this.orderedNodeConnections.iterator();
          return (UnmodifiableIterator<N>)new AbstractIterator<N>(this) {
              @CheckForNull
              protected N computeNext() {
                while (nodeConnections.hasNext()) {
                  DirectedGraphConnections.NodeConnection<N> nodeConnection = nodeConnections.next();
                  if (nodeConnection instanceof DirectedGraphConnections.NodeConnection.Pred)
                    return nodeConnection.node; 
                } 
                return (N)endOfData();
              }
            };
        }
        
        public int size() {
          return DirectedGraphConnections.this.predecessorCount;
        }
        
        public boolean contains(@CheckForNull Object obj) {
          return DirectedGraphConnections.isPredecessor(DirectedGraphConnections.this.adjacentNodeValues.get(obj));
        }
      };
  }
  
  public Set<N> successors() {
    return new AbstractSet<N>() {
        public UnmodifiableIterator<N> iterator() {
          if (DirectedGraphConnections.this.orderedNodeConnections == null) {
            final Iterator<Map.Entry<N, Object>> entries = DirectedGraphConnections.this.adjacentNodeValues.entrySet().iterator();
            return (UnmodifiableIterator<N>)new AbstractIterator<N>(this) {
                @CheckForNull
                protected N computeNext() {
                  while (entries.hasNext()) {
                    Map.Entry<N, Object> entry = entries.next();
                    if (DirectedGraphConnections.isSuccessor(entry.getValue()))
                      return entry.getKey(); 
                  } 
                  return (N)endOfData();
                }
              };
          } 
          final Iterator<DirectedGraphConnections.NodeConnection<N>> nodeConnections = DirectedGraphConnections.this.orderedNodeConnections.iterator();
          return (UnmodifiableIterator<N>)new AbstractIterator<N>(this) {
              @CheckForNull
              protected N computeNext() {
                while (nodeConnections.hasNext()) {
                  DirectedGraphConnections.NodeConnection<N> nodeConnection = nodeConnections.next();
                  if (nodeConnection instanceof DirectedGraphConnections.NodeConnection.Succ)
                    return nodeConnection.node; 
                } 
                return (N)endOfData();
              }
            };
        }
        
        public int size() {
          return DirectedGraphConnections.this.successorCount;
        }
        
        public boolean contains(@CheckForNull Object obj) {
          return DirectedGraphConnections.isSuccessor(DirectedGraphConnections.this.adjacentNodeValues.get(obj));
        }
      };
  }
  
  public Iterator<EndpointPair<N>> incidentEdgeIterator(N thisNode) {
    final Iterator<EndpointPair<N>> resultWithDoubleSelfLoop;
    Preconditions.checkNotNull(thisNode);
    if (this.orderedNodeConnections == null) {
      resultWithDoubleSelfLoop = Iterators.concat(
          Iterators.transform(
            predecessors().iterator(), predecessor -> EndpointPair.ordered(predecessor, thisNode)), 
          
          Iterators.transform(
            successors().iterator(), successor -> EndpointPair.ordered(thisNode, successor)));
    } else {
      resultWithDoubleSelfLoop = Iterators.transform(this.orderedNodeConnections
          .iterator(), connection -> (connection instanceof NodeConnection.Succ) ? EndpointPair.ordered(thisNode, connection.node) : EndpointPair.ordered(connection.node, (N)thisNode));
    } 
    final AtomicBoolean alreadySeenSelfLoop = new AtomicBoolean(false);
    return (Iterator<EndpointPair<N>>)new AbstractIterator<EndpointPair<N>>(this) {
        @CheckForNull
        protected EndpointPair<N> computeNext() {
          while (resultWithDoubleSelfLoop.hasNext()) {
            EndpointPair<N> edge = resultWithDoubleSelfLoop.next();
            if (edge.nodeU().equals(edge.nodeV())) {
              if (!alreadySeenSelfLoop.getAndSet(true))
                return edge; 
              continue;
            } 
            return edge;
          } 
          return (EndpointPair<N>)endOfData();
        }
      };
  }
  
  @CheckForNull
  public V value(N node) {
    Preconditions.checkNotNull(node);
    Object value = this.adjacentNodeValues.get(node);
    if (value == PRED)
      return null; 
    if (value instanceof PredAndSucc)
      return (V)((PredAndSucc)value).successorValue; 
    return (V)value;
  }
  
  public void removePredecessor(N node) {
    boolean removedPredecessor;
    Preconditions.checkNotNull(node);
    Object previousValue = this.adjacentNodeValues.get(node);
    if (previousValue == PRED) {
      this.adjacentNodeValues.remove(node);
      removedPredecessor = true;
    } else if (previousValue instanceof PredAndSucc) {
      this.adjacentNodeValues.put(node, ((PredAndSucc)previousValue).successorValue);
      removedPredecessor = true;
    } else {
      removedPredecessor = false;
    } 
    if (removedPredecessor) {
      Graphs.checkNonNegative(--this.predecessorCount);
      if (this.orderedNodeConnections != null)
        this.orderedNodeConnections.remove(new NodeConnection.Pred<>(node)); 
    } 
  }
  
  @CheckForNull
  public V removeSuccessor(Object node) {
    Object removedValue;
    Preconditions.checkNotNull(node);
    Object previousValue = this.adjacentNodeValues.get(node);
    if (previousValue == null || previousValue == PRED) {
      removedValue = null;
    } else if (previousValue instanceof PredAndSucc) {
      this.adjacentNodeValues.put((N)node, PRED);
      removedValue = ((PredAndSucc)previousValue).successorValue;
    } else {
      this.adjacentNodeValues.remove(node);
      removedValue = previousValue;
    } 
    if (removedValue != null) {
      Graphs.checkNonNegative(--this.successorCount);
      if (this.orderedNodeConnections != null)
        this.orderedNodeConnections.remove(new NodeConnection.Succ(node)); 
    } 
    return (removedValue == null) ? null : (V)removedValue;
  }
  
  public void addPredecessor(N node, V unused) {
    boolean addedPredecessor;
    Object previousValue = this.adjacentNodeValues.put(node, PRED);
    if (previousValue == null) {
      addedPredecessor = true;
    } else if (previousValue instanceof PredAndSucc) {
      this.adjacentNodeValues.put(node, previousValue);
      addedPredecessor = false;
    } else if (previousValue != PRED) {
      this.adjacentNodeValues.put(node, new PredAndSucc(previousValue));
      addedPredecessor = true;
    } else {
      addedPredecessor = false;
    } 
    if (addedPredecessor) {
      Graphs.checkPositive(++this.predecessorCount);
      if (this.orderedNodeConnections != null)
        this.orderedNodeConnections.add(new NodeConnection.Pred<>(node)); 
    } 
  }
  
  @CheckForNull
  public V addSuccessor(N node, V value) {
    Object previousSuccessor, previousValue = this.adjacentNodeValues.put(node, value);
    if (previousValue == null) {
      previousSuccessor = null;
    } else if (previousValue instanceof PredAndSucc) {
      this.adjacentNodeValues.put(node, new PredAndSucc(value));
      previousSuccessor = ((PredAndSucc)previousValue).successorValue;
    } else if (previousValue == PRED) {
      this.adjacentNodeValues.put(node, new PredAndSucc(value));
      previousSuccessor = null;
    } else {
      previousSuccessor = previousValue;
    } 
    if (previousSuccessor == null) {
      Graphs.checkPositive(++this.successorCount);
      if (this.orderedNodeConnections != null)
        this.orderedNodeConnections.add(new NodeConnection.Succ<>(node)); 
    } 
    return (previousSuccessor == null) ? null : (V)previousSuccessor;
  }
  
  private static boolean isPredecessor(@CheckForNull Object value) {
    return (value == PRED || value instanceof PredAndSucc);
  }
  
  private static boolean isSuccessor(@CheckForNull Object value) {
    return (value != PRED && value != null);
  }
}
