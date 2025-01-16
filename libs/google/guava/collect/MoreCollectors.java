package me.syncwrld.booter.libs.google.guava.collect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collector;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public final class MoreCollectors {
  private static final Collector<Object, ?, Optional<Object>> TO_OPTIONAL = Collector.of(ToOptionalState::new, ToOptionalState::add, ToOptionalState::combine, ToOptionalState::getOptional, new Collector.Characteristics[] { Collector.Characteristics.UNORDERED });
  
  public static <T> Collector<T, ?, Optional<T>> toOptional() {
    return (Collector)TO_OPTIONAL;
  }
  
  private static final Object NULL_PLACEHOLDER = new Object();
  
  private static final Collector<Object, ?, Object> ONLY_ELEMENT;
  
  static {
    ONLY_ELEMENT = Collector.of(ToOptionalState::new, (state, o) -> state.add((o == null) ? NULL_PLACEHOLDER : o), ToOptionalState::combine, state -> {
          Object result = state.getElement();
          return (result == NULL_PLACEHOLDER) ? null : result;
        }new Collector.Characteristics[] { Collector.Characteristics.UNORDERED });
  }
  
  public static <T> Collector<T, ?, T> onlyElement() {
    return (Collector)ONLY_ELEMENT;
  }
  
  private static final class ToOptionalState {
    static final int MAX_EXTRAS = 4;
    
    @CheckForNull
    Object element = null;
    
    List<Object> extras = Collections.emptyList();
    
    IllegalArgumentException multiples(boolean overflow) {
      StringBuilder sb = (new StringBuilder()).append("expected one element but was: <").append(this.element);
      for (Object o : this.extras)
        sb.append(", ").append(o); 
      if (overflow)
        sb.append(", ..."); 
      sb.append('>');
      throw new IllegalArgumentException(sb.toString());
    }
    
    void add(Object o) {
      Preconditions.checkNotNull(o);
      if (this.element == null) {
        this.element = o;
      } else if (this.extras.isEmpty()) {
        this.extras = new ArrayList(4);
        this.extras.add(o);
      } else if (this.extras.size() < 4) {
        this.extras.add(o);
      } else {
        throw multiples(true);
      } 
    }
    
    ToOptionalState combine(ToOptionalState other) {
      if (this.element == null)
        return other; 
      if (other.element == null)
        return this; 
      if (this.extras.isEmpty())
        this.extras = new ArrayList(); 
      this.extras.add(other.element);
      this.extras.addAll(other.extras);
      if (this.extras.size() > 4) {
        this.extras.subList(4, this.extras.size()).clear();
        throw multiples(true);
      } 
      return this;
    }
    
    Optional<Object> getOptional() {
      if (this.extras.isEmpty())
        return Optional.ofNullable(this.element); 
      throw multiples(false);
    }
    
    Object getElement() {
      if (this.element == null)
        throw new NoSuchElementException(); 
      if (this.extras.isEmpty())
        return this.element; 
      throw multiples(false);
    }
  }
}
