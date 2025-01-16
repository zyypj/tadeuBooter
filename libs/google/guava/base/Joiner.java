package me.syncwrld.booter.libs.google.guava.base;

import java.io.IOException;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public class Joiner {
  private final String separator;
  
  public static Joiner on(String separator) {
    return new Joiner(separator);
  }
  
  public static Joiner on(char separator) {
    return new Joiner(String.valueOf(separator));
  }
  
  private Joiner(String separator) {
    this.separator = Preconditions.<String>checkNotNull(separator);
  }
  
  private Joiner(Joiner prototype) {
    this.separator = prototype.separator;
  }
  
  @CanIgnoreReturnValue
  public <A extends Appendable> A appendTo(A appendable, Iterable<? extends Object> parts) throws IOException {
    return appendTo(appendable, parts.iterator());
  }
  
  @CanIgnoreReturnValue
  public <A extends Appendable> A appendTo(A appendable, Iterator<? extends Object> parts) throws IOException {
    Preconditions.checkNotNull(appendable);
    if (parts.hasNext()) {
      appendable.append(toString(parts.next()));
      while (parts.hasNext()) {
        appendable.append(this.separator);
        appendable.append(toString(parts.next()));
      } 
    } 
    return appendable;
  }
  
  @CanIgnoreReturnValue
  public final <A extends Appendable> A appendTo(A appendable, Object[] parts) throws IOException {
    List<?> partsList = Arrays.asList(parts);
    return appendTo(appendable, (Iterable)partsList);
  }
  
  @CanIgnoreReturnValue
  public final <A extends Appendable> A appendTo(A appendable, @CheckForNull Object first, @CheckForNull Object second, Object... rest) throws IOException {
    return appendTo(appendable, iterable(first, second, rest));
  }
  
  @CanIgnoreReturnValue
  public final StringBuilder appendTo(StringBuilder builder, Iterable<? extends Object> parts) {
    return appendTo(builder, parts.iterator());
  }
  
  @CanIgnoreReturnValue
  public final StringBuilder appendTo(StringBuilder builder, Iterator<? extends Object> parts) {
    try {
      appendTo(builder, parts);
    } catch (IOException impossible) {
      throw new AssertionError(impossible);
    } 
    return builder;
  }
  
  @CanIgnoreReturnValue
  public final StringBuilder appendTo(StringBuilder builder, Object[] parts) {
    List<?> partsList = Arrays.asList(parts);
    return appendTo(builder, (Iterable)partsList);
  }
  
  @CanIgnoreReturnValue
  public final StringBuilder appendTo(StringBuilder builder, @CheckForNull Object first, @CheckForNull Object second, Object... rest) {
    return appendTo(builder, iterable(first, second, rest));
  }
  
  public final String join(Iterable<? extends Object> parts) {
    return join(parts.iterator());
  }
  
  public final String join(Iterator<? extends Object> parts) {
    return appendTo(new StringBuilder(), parts).toString();
  }
  
  public final String join(Object[] parts) {
    List<?> partsList = Arrays.asList(parts);
    return join((Iterable)partsList);
  }
  
  public final String join(@CheckForNull Object first, @CheckForNull Object second, Object... rest) {
    return join(iterable(first, second, rest));
  }
  
  public Joiner useForNull(final String nullText) {
    Preconditions.checkNotNull(nullText);
    return new Joiner(this) {
        CharSequence toString(@CheckForNull Object part) {
          return (part == null) ? nullText : Joiner.this.toString(part);
        }
        
        public Joiner useForNull(String nullText) {
          throw new UnsupportedOperationException("already specified useForNull");
        }
        
        public Joiner skipNulls() {
          throw new UnsupportedOperationException("already specified useForNull");
        }
      };
  }
  
  public Joiner skipNulls() {
    return new Joiner(this) {
        public <A extends Appendable> A appendTo(A appendable, Iterator<? extends Object> parts) throws IOException {
          Preconditions.checkNotNull(appendable, "appendable");
          Preconditions.checkNotNull(parts, "parts");
          while (parts.hasNext()) {
            Object part = parts.next();
            if (part != null) {
              appendable.append(Joiner.this.toString(part));
              break;
            } 
          } 
          while (parts.hasNext()) {
            Object part = parts.next();
            if (part != null) {
              appendable.append(Joiner.this.separator);
              appendable.append(Joiner.this.toString(part));
            } 
          } 
          return appendable;
        }
        
        public Joiner useForNull(String nullText) {
          throw new UnsupportedOperationException("already specified skipNulls");
        }
        
        public Joiner.MapJoiner withKeyValueSeparator(String kvs) {
          throw new UnsupportedOperationException("can't use .skipNulls() with maps");
        }
      };
  }
  
  public MapJoiner withKeyValueSeparator(char keyValueSeparator) {
    return withKeyValueSeparator(String.valueOf(keyValueSeparator));
  }
  
  public MapJoiner withKeyValueSeparator(String keyValueSeparator) {
    return new MapJoiner(this, keyValueSeparator);
  }
  
  public static final class MapJoiner {
    private final Joiner joiner;
    
    private final String keyValueSeparator;
    
    private MapJoiner(Joiner joiner, String keyValueSeparator) {
      this.joiner = joiner;
      this.keyValueSeparator = Preconditions.<String>checkNotNull(keyValueSeparator);
    }
    
    @CanIgnoreReturnValue
    public <A extends Appendable> A appendTo(A appendable, Map<?, ?> map) throws IOException {
      return appendTo(appendable, map.entrySet());
    }
    
    @CanIgnoreReturnValue
    public StringBuilder appendTo(StringBuilder builder, Map<?, ?> map) {
      return appendTo(builder, map.entrySet());
    }
    
    @CanIgnoreReturnValue
    public <A extends Appendable> A appendTo(A appendable, Iterable<? extends Map.Entry<?, ?>> entries) throws IOException {
      return appendTo(appendable, entries.iterator());
    }
    
    @CanIgnoreReturnValue
    public <A extends Appendable> A appendTo(A appendable, Iterator<? extends Map.Entry<?, ?>> parts) throws IOException {
      Preconditions.checkNotNull(appendable);
      if (parts.hasNext()) {
        Map.Entry<?, ?> entry = parts.next();
        appendable.append(this.joiner.toString(entry.getKey()));
        appendable.append(this.keyValueSeparator);
        appendable.append(this.joiner.toString(entry.getValue()));
        while (parts.hasNext()) {
          appendable.append(this.joiner.separator);
          Map.Entry<?, ?> e = parts.next();
          appendable.append(this.joiner.toString(e.getKey()));
          appendable.append(this.keyValueSeparator);
          appendable.append(this.joiner.toString(e.getValue()));
        } 
      } 
      return appendable;
    }
    
    @CanIgnoreReturnValue
    public StringBuilder appendTo(StringBuilder builder, Iterable<? extends Map.Entry<?, ?>> entries) {
      return appendTo(builder, entries.iterator());
    }
    
    @CanIgnoreReturnValue
    public StringBuilder appendTo(StringBuilder builder, Iterator<? extends Map.Entry<?, ?>> entries) {
      try {
        appendTo(builder, entries);
      } catch (IOException impossible) {
        throw new AssertionError(impossible);
      } 
      return builder;
    }
    
    public String join(Map<?, ?> map) {
      return join(map.entrySet());
    }
    
    public String join(Iterable<? extends Map.Entry<?, ?>> entries) {
      return join(entries.iterator());
    }
    
    public String join(Iterator<? extends Map.Entry<?, ?>> entries) {
      return appendTo(new StringBuilder(), entries).toString();
    }
    
    public MapJoiner useForNull(String nullText) {
      return new MapJoiner(this.joiner.useForNull(nullText), this.keyValueSeparator);
    }
  }
  
  CharSequence toString(@CheckForNull Object part) {
    Objects.requireNonNull(part);
    return (part instanceof CharSequence) ? (CharSequence)part : part.toString();
  }
  
  private static Iterable<Object> iterable(@CheckForNull final Object first, @CheckForNull final Object second, final Object[] rest) {
    Preconditions.checkNotNull(rest);
    return new AbstractList() {
        public int size() {
          return rest.length + 2;
        }
        
        @CheckForNull
        public Object get(int index) {
          switch (index) {
            case 0:
              return first;
            case 1:
              return second;
          } 
          return rest[index - 2];
        }
      };
  }
}
