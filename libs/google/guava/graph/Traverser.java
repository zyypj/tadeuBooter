package me.syncwrld.booter.libs.google.guava.graph;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import me.syncwrld.booter.libs.google.errorprone.annotations.DoNotMock;
import me.syncwrld.booter.libs.google.guava.annotations.Beta;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.guava.collect.AbstractIterator;
import me.syncwrld.booter.libs.google.guava.collect.ImmutableSet;
import me.syncwrld.booter.libs.google.guava.collect.UnmodifiableIterator;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@DoNotMock("Call forGraph or forTree, passing a lambda or a Graph with the desired edges (built with GraphBuilder)")
@ElementTypesAreNonnullByDefault
@Beta
public abstract class Traverser<N> {
  private final SuccessorsFunction<N> successorFunction;
  
  private Traverser(SuccessorsFunction<N> successorFunction) {
    this.successorFunction = (SuccessorsFunction<N>)Preconditions.checkNotNull(successorFunction);
  }
  
  public static <N> Traverser<N> forGraph(final SuccessorsFunction<N> graph) {
    return new Traverser<N>(graph) {
        Traverser.Traversal<N> newTraversal() {
          return Traverser.Traversal.inGraph(graph);
        }
      };
  }
  
  public static <N> Traverser<N> forTree(final SuccessorsFunction<N> tree) {
    if (tree instanceof BaseGraph)
      Preconditions.checkArgument(((BaseGraph)tree).isDirected(), "Undirected graphs can never be trees."); 
    if (tree instanceof Network)
      Preconditions.checkArgument(((Network)tree).isDirected(), "Undirected networks can never be trees."); 
    return new Traverser<N>(tree) {
        Traverser.Traversal<N> newTraversal() {
          return Traverser.Traversal.inTree(tree);
        }
      };
  }
  
  public final Iterable<N> breadthFirst(N startNode) {
    return breadthFirst((Iterable<? extends N>)ImmutableSet.of(startNode));
  }
  
  public final Iterable<N> breadthFirst(Iterable<? extends N> startNodes) {
    final ImmutableSet<N> validated = validate(startNodes);
    return new Iterable<N>() {
        public Iterator<N> iterator() {
          return Traverser.this.newTraversal().breadthFirst((Iterator<? extends N>)validated.iterator());
        }
      };
  }
  
  public final Iterable<N> depthFirstPreOrder(N startNode) {
    return depthFirstPreOrder((Iterable<? extends N>)ImmutableSet.of(startNode));
  }
  
  public final Iterable<N> depthFirstPreOrder(Iterable<? extends N> startNodes) {
    final ImmutableSet<N> validated = validate(startNodes);
    return new Iterable<N>() {
        public Iterator<N> iterator() {
          return Traverser.this.newTraversal().preOrder((Iterator<? extends N>)validated.iterator());
        }
      };
  }
  
  public final Iterable<N> depthFirstPostOrder(N startNode) {
    return depthFirstPostOrder((Iterable<? extends N>)ImmutableSet.of(startNode));
  }
  
  public final Iterable<N> depthFirstPostOrder(Iterable<? extends N> startNodes) {
    final ImmutableSet<N> validated = validate(startNodes);
    return new Iterable<N>() {
        public Iterator<N> iterator() {
          return Traverser.this.newTraversal().postOrder((Iterator<? extends N>)validated.iterator());
        }
      };
  }
  
  private ImmutableSet<N> validate(Iterable<? extends N> startNodes) {
    ImmutableSet<N> copy = ImmutableSet.copyOf(startNodes);
    for (UnmodifiableIterator<N> unmodifiableIterator = copy.iterator(); unmodifiableIterator.hasNext(); ) {
      N node = unmodifiableIterator.next();
      this.successorFunction.successors(node);
    } 
    return copy;
  }
  
  abstract Traversal<N> newTraversal();
  
  private static abstract class Traversal<N> {
    final SuccessorsFunction<N> successorFunction;
    
    Traversal(SuccessorsFunction<N> successorFunction) {
      this.successorFunction = successorFunction;
    }
    
    static <N> Traversal<N> inGraph(SuccessorsFunction<N> graph) {
      final Set<N> visited = new HashSet<>();
      return new Traversal<N>(graph) {
          @CheckForNull
          N visitNext(Deque<Iterator<? extends N>> horizon) {
            Iterator<? extends N> top = horizon.getFirst();
            while (top.hasNext()) {
              N element = top.next();
              Objects.requireNonNull(element);
              if (visited.add(element))
                return element; 
            } 
            horizon.removeFirst();
            return null;
          }
        };
    }
    
    static <N> Traversal<N> inTree(SuccessorsFunction<N> tree) {
      return new Traversal<N>(tree) {
          @CheckForNull
          N visitNext(Deque<Iterator<? extends N>> horizon) {
            Iterator<? extends N> top = horizon.getFirst();
            if (top.hasNext())
              return (N)Preconditions.checkNotNull(top.next()); 
            horizon.removeFirst();
            return null;
          }
        };
    }
    
    final Iterator<N> breadthFirst(Iterator<? extends N> startNodes) {
      return topDown(startNodes, Traverser.InsertionOrder.BACK);
    }
    
    final Iterator<N> preOrder(Iterator<? extends N> startNodes) {
      return topDown(startNodes, Traverser.InsertionOrder.FRONT);
    }
    
    private Iterator<N> topDown(Iterator<? extends N> startNodes, final Traverser.InsertionOrder order) {
      final Deque<Iterator<? extends N>> horizon = new ArrayDeque<>();
      horizon.add(startNodes);
      return (Iterator<N>)new AbstractIterator<N>() {
          @CheckForNull
          protected N computeNext() {
            while (true) {
              N next = Traverser.Traversal.this.visitNext(horizon);
              if (next != null) {
                Iterator<? extends N> successors = Traverser.Traversal.this.successorFunction.successors(next).iterator();
                if (successors.hasNext())
                  order.insertInto(horizon, successors); 
                return next;
              } 
              if (horizon.isEmpty())
                return (N)endOfData(); 
            } 
          }
        };
    }
    
    final Iterator<N> postOrder(Iterator<? extends N> startNodes) {
      final Deque<N> ancestorStack = new ArrayDeque<>();
      final Deque<Iterator<? extends N>> horizon = new ArrayDeque<>();
      horizon.add(startNodes);
      return (Iterator<N>)new AbstractIterator<N>() {
          @CheckForNull
          protected N computeNext() {
            for (N next = Traverser.Traversal.this.visitNext(horizon); next != null; next = Traverser.Traversal.this.visitNext(horizon)) {
              Iterator<? extends N> successors = Traverser.Traversal.this.successorFunction.successors(next).iterator();
              if (!successors.hasNext())
                return next; 
              horizon.addFirst(successors);
              ancestorStack.push(next);
            } 
            if (!ancestorStack.isEmpty())
              return ancestorStack.pop(); 
            return (N)endOfData();
          }
        };
    }
    
    @CheckForNull
    abstract N visitNext(Deque<Iterator<? extends N>> param1Deque);
  }
  
  private enum InsertionOrder {
    FRONT {
      <T> void insertInto(Deque<T> deque, T value) {
        deque.addFirst(value);
      }
    },
    BACK {
      <T> void insertInto(Deque<T> deque, T value) {
        deque.addLast(value);
      }
    };
    
    abstract <T> void insertInto(Deque<T> param1Deque, T param1T);
  }
}
