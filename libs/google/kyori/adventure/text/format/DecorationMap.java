package me.syncwrld.booter.libs.google.kyori.adventure.text.format;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterators;
import java.util.stream.Stream;
import me.syncwrld.booter.libs.google.kyori.examination.Examinable;
import me.syncwrld.booter.libs.google.kyori.examination.ExaminableProperty;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Unmodifiable;

@Unmodifiable
final class DecorationMap extends AbstractMap<TextDecoration, TextDecoration.State> implements Examinable {
  static final TextDecoration[] DECORATIONS = TextDecoration.values();
  
  private static final TextDecoration.State[] STATES = TextDecoration.State.values();
  
  private static final int MAP_SIZE = DECORATIONS.length;
  
  private static final TextDecoration.State[] EMPTY_STATE_ARRAY = new TextDecoration.State[0];
  
  static final DecorationMap EMPTY = new DecorationMap(0);
  
  private static final KeySet KEY_SET = new KeySet();
  
  private final int bitSet;
  
  static DecorationMap fromMap(Map<TextDecoration, TextDecoration.State> decorationMap) {
    if (decorationMap instanceof DecorationMap)
      return (DecorationMap)decorationMap; 
    int bitSet = 0;
    for (TextDecoration decoration : DECORATIONS)
      bitSet |= ((TextDecoration.State)decorationMap.getOrDefault(decoration, TextDecoration.State.NOT_SET)).ordinal() * offset(decoration); 
    return withBitSet(bitSet);
  }
  
  static DecorationMap merge(Map<TextDecoration, TextDecoration.State> first, Map<TextDecoration, TextDecoration.State> second) {
    int bitSet = 0;
    for (TextDecoration decoration : DECORATIONS)
      bitSet |= ((TextDecoration.State)first.getOrDefault(decoration, second.getOrDefault(decoration, TextDecoration.State.NOT_SET))).ordinal() * offset(decoration); 
    return withBitSet(bitSet);
  }
  
  private static DecorationMap withBitSet(int bitSet) {
    return (bitSet == 0) ? EMPTY : new DecorationMap(bitSet);
  }
  
  private static int offset(TextDecoration decoration) {
    return 1 << decoration.ordinal() * 2;
  }
  
  private volatile EntrySet entrySet = null;
  
  private volatile Values values = null;
  
  private DecorationMap(int bitSet) {
    this.bitSet = bitSet;
  }
  
  @NotNull
  public DecorationMap with(@NotNull TextDecoration decoration, TextDecoration.State state) {
    Objects.requireNonNull(state, "state");
    Objects.requireNonNull(decoration, "decoration");
    int offset = offset(decoration);
    return withBitSet(this.bitSet & (3 * offset ^ 0xFFFFFFFF) | state.ordinal() * offset);
  }
  
  @NotNull
  public Stream<? extends ExaminableProperty> examinableProperties() {
    return Arrays.<TextDecoration>stream(DECORATIONS)
      .map(decoration -> ExaminableProperty.of(decoration.toString(), get(decoration)));
  }
  
  public TextDecoration.State get(Object o) {
    if (o instanceof TextDecoration)
      return STATES[this.bitSet >> ((TextDecoration)o).ordinal() * 2 & 0x3]; 
    return null;
  }
  
  public boolean containsKey(Object key) {
    return key instanceof TextDecoration;
  }
  
  public int size() {
    return MAP_SIZE;
  }
  
  public boolean isEmpty() {
    return false;
  }
  
  @NotNull
  public Set<Map.Entry<TextDecoration, TextDecoration.State>> entrySet() {
    if (this.entrySet == null)
      synchronized (this) {
        if (this.entrySet == null)
          this.entrySet = new EntrySet(); 
      }  
    return this.entrySet;
  }
  
  @NotNull
  public Set<TextDecoration> keySet() {
    return KEY_SET;
  }
  
  @NotNull
  public Collection<TextDecoration.State> values() {
    if (this.values == null)
      synchronized (this) {
        if (this.values == null)
          this.values = new Values(); 
      }  
    return this.values;
  }
  
  public boolean equals(Object other) {
    if (other == this)
      return true; 
    if (other == null || other.getClass() != DecorationMap.class)
      return false; 
    return (this.bitSet == ((DecorationMap)other).bitSet);
  }
  
  public int hashCode() {
    return this.bitSet;
  }
  
  final class EntrySet extends AbstractSet<Map.Entry<TextDecoration, TextDecoration.State>> {
    @NotNull
    public Iterator<Map.Entry<TextDecoration, TextDecoration.State>> iterator() {
      return new Iterator<Map.Entry<TextDecoration, TextDecoration.State>>() {
          private final Iterator<TextDecoration> decorations = DecorationMap.KEY_SET.iterator();
          
          private final Iterator<TextDecoration.State> states = DecorationMap.this.values().iterator();
          
          public boolean hasNext() {
            return (this.decorations.hasNext() && this.states.hasNext());
          }
          
          public Map.Entry<TextDecoration, TextDecoration.State> next() {
            if (hasNext())
              return new AbstractMap.SimpleImmutableEntry<>(this.decorations.next(), this.states.next()); 
            throw new NoSuchElementException();
          }
        };
    }
    
    public int size() {
      return DecorationMap.MAP_SIZE;
    }
  }
  
  final class Values extends AbstractCollection<TextDecoration.State> {
    @NotNull
    public Iterator<TextDecoration.State> iterator() {
      return Spliterators.iterator(Arrays.spliterator(toArray(DecorationMap.EMPTY_STATE_ARRAY)));
    }
    
    public boolean isEmpty() {
      return false;
    }
    
    public Object[] toArray() {
      Object[] states = new Object[DecorationMap.MAP_SIZE];
      for (int i = 0; i < DecorationMap.MAP_SIZE; i++)
        states[i] = DecorationMap.this.get(DecorationMap.DECORATIONS[i]); 
      return states;
    }
    
    public <T> T[] toArray(T[] dest) {
      if (dest.length < DecorationMap.MAP_SIZE)
        return Arrays.copyOf(toArray(), DecorationMap.MAP_SIZE, (Class)dest.getClass()); 
      System.arraycopy(toArray(), 0, dest, 0, DecorationMap.MAP_SIZE);
      if (dest.length > DecorationMap.MAP_SIZE)
        dest[DecorationMap.MAP_SIZE] = null; 
      return dest;
    }
    
    public boolean contains(Object o) {
      return (o instanceof TextDecoration.State && super.contains(o));
    }
    
    public int size() {
      return DecorationMap.MAP_SIZE;
    }
  }
  
  static final class KeySet extends AbstractSet<TextDecoration> {
    public boolean contains(Object o) {
      return o instanceof TextDecoration;
    }
    
    public boolean isEmpty() {
      return false;
    }
    
    public Object[] toArray() {
      return Arrays.copyOf(DecorationMap.DECORATIONS, DecorationMap.MAP_SIZE, Object[].class);
    }
    
    public <T> T[] toArray(T[] dest) {
      if (dest.length < DecorationMap.MAP_SIZE)
        return Arrays.copyOf(DecorationMap.DECORATIONS, DecorationMap.MAP_SIZE, (Class)dest.getClass()); 
      System.arraycopy(DecorationMap.DECORATIONS, 0, dest, 0, DecorationMap.MAP_SIZE);
      if (dest.length > DecorationMap.MAP_SIZE)
        dest[DecorationMap.MAP_SIZE] = null; 
      return dest;
    }
    
    @NotNull
    public Iterator<TextDecoration> iterator() {
      return Spliterators.iterator(Arrays.spliterator(DecorationMap.DECORATIONS));
    }
    
    public int size() {
      return DecorationMap.MAP_SIZE;
    }
  }
}
