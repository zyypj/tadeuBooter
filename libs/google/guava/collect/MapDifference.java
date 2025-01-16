package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Map;
import me.syncwrld.booter.libs.google.errorprone.annotations.DoNotMock;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@DoNotMock("Use Maps.difference")
@ElementTypesAreNonnullByDefault
@GwtCompatible
public interface MapDifference<K, V> {
  boolean areEqual();
  
  Map<K, V> entriesOnlyOnLeft();
  
  Map<K, V> entriesOnlyOnRight();
  
  Map<K, V> entriesInCommon();
  
  Map<K, ValueDifference<V>> entriesDiffering();
  
  boolean equals(@CheckForNull Object paramObject);
  
  int hashCode();
  
  @DoNotMock("Use Maps.difference")
  public static interface ValueDifference<V> {
    @ParametricNullness
    V leftValue();
    
    @ParametricNullness
    V rightValue();
    
    boolean equals(@CheckForNull Object param1Object);
    
    int hashCode();
  }
}
