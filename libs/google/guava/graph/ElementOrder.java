package me.syncwrld.booter.libs.google.guava.graph;

import java.util.Comparator;
import java.util.Map;
import me.syncwrld.booter.libs.google.errorprone.annotations.Immutable;
import me.syncwrld.booter.libs.google.guava.annotations.Beta;
import me.syncwrld.booter.libs.google.guava.base.MoreObjects;
import me.syncwrld.booter.libs.google.guava.base.Objects;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.guava.collect.Maps;
import me.syncwrld.booter.libs.google.guava.collect.Ordering;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@Immutable
@ElementTypesAreNonnullByDefault
@Beta
public final class ElementOrder<T> {
  private final Type type;
  
  @CheckForNull
  private final Comparator<T> comparator;
  
  public enum Type {
    UNORDERED, STABLE, INSERTION, SORTED;
  }
  
  private ElementOrder(Type type, @CheckForNull Comparator<T> comparator) {
    this.type = (Type)Preconditions.checkNotNull(type);
    this.comparator = comparator;
    Preconditions.checkState((((type == Type.SORTED) ? true : false) == ((comparator != null) ? true : false)));
  }
  
  public static <S> ElementOrder<S> unordered() {
    return new ElementOrder<>(Type.UNORDERED, null);
  }
  
  public static <S> ElementOrder<S> stable() {
    return new ElementOrder<>(Type.STABLE, null);
  }
  
  public static <S> ElementOrder<S> insertion() {
    return new ElementOrder<>(Type.INSERTION, null);
  }
  
  public static <S extends Comparable<? super S>> ElementOrder<S> natural() {
    return new ElementOrder<>(Type.SORTED, (Comparator<S>)Ordering.natural());
  }
  
  public static <S> ElementOrder<S> sorted(Comparator<S> comparator) {
    return new ElementOrder<>(Type.SORTED, (Comparator<S>)Preconditions.checkNotNull(comparator));
  }
  
  public Type type() {
    return this.type;
  }
  
  public Comparator<T> comparator() {
    if (this.comparator != null)
      return this.comparator; 
    throw new UnsupportedOperationException("This ordering does not define a comparator.");
  }
  
  public boolean equals(@CheckForNull Object obj) {
    if (obj == this)
      return true; 
    if (!(obj instanceof ElementOrder))
      return false; 
    ElementOrder<?> other = (ElementOrder)obj;
    return (this.type == other.type && Objects.equal(this.comparator, other.comparator));
  }
  
  public int hashCode() {
    return Objects.hashCode(new Object[] { this.type, this.comparator });
  }
  
  public String toString() {
    MoreObjects.ToStringHelper helper = MoreObjects.toStringHelper(this).add("type", this.type);
    if (this.comparator != null)
      helper.add("comparator", this.comparator); 
    return helper.toString();
  }
  
  <K extends T, V> Map<K, V> createMap(int expectedSize) {
    switch (this.type) {
      case UNORDERED:
        return Maps.newHashMapWithExpectedSize(expectedSize);
      case INSERTION:
      case STABLE:
        return Maps.newLinkedHashMapWithExpectedSize(expectedSize);
      case SORTED:
        return Maps.newTreeMap(comparator());
    } 
    throw new AssertionError();
  }
  
  <T1 extends T> ElementOrder<T1> cast() {
    return this;
  }
}
