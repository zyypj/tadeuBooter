package me.syncwrld.booter.libs.google.guava.collect;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.guava.primitives.Primitives;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
public final class MutableClassToInstanceMap<B> extends ForwardingMap<Class<? extends B>, B> implements ClassToInstanceMap<B>, Serializable {
  private final Map<Class<? extends B>, B> delegate;
  
  public static <B> MutableClassToInstanceMap<B> create() {
    return new MutableClassToInstanceMap<>(new HashMap<>());
  }
  
  public static <B> MutableClassToInstanceMap<B> create(Map<Class<? extends B>, B> backingMap) {
    return new MutableClassToInstanceMap<>(backingMap);
  }
  
  private MutableClassToInstanceMap(Map<Class<? extends B>, B> delegate) {
    this.delegate = (Map<Class<? extends B>, B>)Preconditions.checkNotNull(delegate);
  }
  
  protected Map<Class<? extends B>, B> delegate() {
    return this.delegate;
  }
  
  private static <B> Map.Entry<Class<? extends B>, B> checkedEntry(final Map.Entry<Class<? extends B>, B> entry) {
    return new ForwardingMapEntry<Class<? extends B>, B>() {
        protected Map.Entry<Class<? extends B>, B> delegate() {
          return entry;
        }
        
        @ParametricNullness
        public B setValue(@ParametricNullness B value) {
          MutableClassToInstanceMap.cast((Class)getKey(), value);
          return super.setValue(value);
        }
      };
  }
  
  public Set<Map.Entry<Class<? extends B>, B>> entrySet() {
    return new ForwardingSet<Map.Entry<Class<? extends B>, B>>() {
        protected Set<Map.Entry<Class<? extends B>, B>> delegate() {
          return MutableClassToInstanceMap.this.delegate().entrySet();
        }
        
        public Spliterator<Map.Entry<Class<? extends B>, B>> spliterator() {
          return CollectSpliterators.map(
              delegate().spliterator(), x$0 -> MutableClassToInstanceMap.checkedEntry(x$0));
        }
        
        public Iterator<Map.Entry<Class<? extends B>, B>> iterator() {
          return new TransformedIterator<Map.Entry<Class<? extends B>, B>, Map.Entry<Class<? extends B>, B>>(this, 
              
              delegate().iterator()) {
              Map.Entry<Class<? extends B>, B> transform(Map.Entry<Class<? extends B>, B> from) {
                return MutableClassToInstanceMap.checkedEntry(from);
              }
            };
        }
        
        public Object[] toArray() {
          Object[] result = standardToArray();
          return result;
        }
        
        public <T> T[] toArray(T[] array) {
          return (T[])standardToArray((Object[])array);
        }
      };
  }
  
  @CheckForNull
  @CanIgnoreReturnValue
  public B put(Class<? extends B> key, @ParametricNullness B value) {
    cast(key, value);
    return super.put(key, value);
  }
  
  public void putAll(Map<? extends Class<? extends B>, ? extends B> map) {
    Map<Class<? extends B>, B> copy = new LinkedHashMap<>(map);
    for (Map.Entry<? extends Class<? extends B>, B> entry : copy.entrySet())
      cast(entry.getKey(), entry.getValue()); 
    super.putAll(copy);
  }
  
  @CheckForNull
  @CanIgnoreReturnValue
  public <T extends B> T putInstance(Class<T> type, @ParametricNullness T value) {
    return cast(type, put(type, (B)value));
  }
  
  @CheckForNull
  public <T extends B> T getInstance(Class<T> type) {
    return cast(type, get(type));
  }
  
  @CheckForNull
  @CanIgnoreReturnValue
  private static <T> T cast(Class<T> type, @CheckForNull Object value) {
    return Primitives.wrap(type).cast(value);
  }
  
  private Object writeReplace() {
    return new SerializedForm<>(delegate());
  }
  
  private void readObject(ObjectInputStream stream) throws InvalidObjectException {
    throw new InvalidObjectException("Use SerializedForm");
  }
  
  private static final class SerializedForm<B> implements Serializable {
    private final Map<Class<? extends B>, B> backingMap;
    
    private static final long serialVersionUID = 0L;
    
    SerializedForm(Map<Class<? extends B>, B> backingMap) {
      this.backingMap = backingMap;
    }
    
    Object readResolve() {
      return MutableClassToInstanceMap.create(this.backingMap);
    }
  }
}
