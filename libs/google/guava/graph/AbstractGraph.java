package me.syncwrld.booter.libs.google.guava.graph;

import java.util.Set;
import me.syncwrld.booter.libs.google.guava.annotations.Beta;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@Beta
public abstract class AbstractGraph<N> extends AbstractBaseGraph<N> implements Graph<N> {
  public final boolean equals(@CheckForNull Object obj) {
    if (obj == this)
      return true; 
    if (!(obj instanceof Graph))
      return false; 
    Graph<?> other = (Graph)obj;
    return (isDirected() == other.isDirected() && 
      nodes().equals(other.nodes()) && 
      edges().equals(other.edges()));
  }
  
  public final int hashCode() {
    return edges().hashCode();
  }
  
  public String toString() {
    return "isDirected: " + 
      isDirected() + ", allowsSelfLoops: " + 
      
      allowsSelfLoops() + ", nodes: " + 
      
      nodes() + ", edges: " + 
      
      edges();
  }
}
