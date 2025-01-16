package me.syncwrld.booter.libs.google.guava.collect;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractSequentialList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible(serializable = true, emulated = true)
public class LinkedListMultimap<K, V> extends AbstractMultimap<K, V> implements ListMultimap<K, V>, Serializable {
  @CheckForNull
  private transient Node<K, V> head;
  
  @CheckForNull
  private transient Node<K, V> tail;
  
  private transient Map<K, KeyList<K, V>> keyToKeyList;
  
  private transient int size;
  
  private transient int modCount;
  
  @GwtIncompatible
  @J2ktIncompatible
  private static final long serialVersionUID = 0L;
  
  static final class Node<K, V> extends AbstractMapEntry<K, V> {
    @ParametricNullness
    final K key;
    
    @ParametricNullness
    V value;
    
    @CheckForNull
    Node<K, V> next;
    
    @CheckForNull
    Node<K, V> previous;
    
    @CheckForNull
    Node<K, V> nextSibling;
    
    @CheckForNull
    Node<K, V> previousSibling;
    
    Node(@ParametricNullness K key, @ParametricNullness V value) {
      this.key = key;
      this.value = value;
    }
    
    @ParametricNullness
    public K getKey() {
      return this.key;
    }
    
    @ParametricNullness
    public V getValue() {
      return this.value;
    }
    
    @ParametricNullness
    public V setValue(@ParametricNullness V newValue) {
      V result = this.value;
      this.value = newValue;
      return result;
    }
  }
  
  private static class KeyList<K, V> {
    LinkedListMultimap.Node<K, V> head;
    
    LinkedListMultimap.Node<K, V> tail;
    
    int count;
    
    KeyList(LinkedListMultimap.Node<K, V> firstNode) {
      this.head = firstNode;
      this.tail = firstNode;
      firstNode.previousSibling = null;
      firstNode.nextSibling = null;
      this.count = 1;
    }
  }
  
  public static <K, V> LinkedListMultimap<K, V> create() {
    return new LinkedListMultimap<>();
  }
  
  public static <K, V> LinkedListMultimap<K, V> create(int expectedKeys) {
    return new LinkedListMultimap<>(expectedKeys);
  }
  
  public static <K, V> LinkedListMultimap<K, V> create(Multimap<? extends K, ? extends V> multimap) {
    return new LinkedListMultimap<>(multimap);
  }
  
  LinkedListMultimap() {
    this(12);
  }
  
  private LinkedListMultimap(int expectedKeys) {
    this.keyToKeyList = Platform.newHashMapWithExpectedSize(expectedKeys);
  }
  
  private LinkedListMultimap(Multimap<? extends K, ? extends V> multimap) {
    this(multimap.keySet().size());
    putAll(multimap);
  }
  
  @CanIgnoreReturnValue
  private Node<K, V> addNode(@ParametricNullness K key, @ParametricNullness V value, @CheckForNull Node<K, V> nextSibling) {
    Node<K, V> node = new Node<>(key, value);
    if (this.head == null) {
      this.head = this.tail = node;
      this.keyToKeyList.put(key, new KeyList<>(node));
      this.modCount++;
    } else if (nextSibling == null) {
      ((Node)Objects.requireNonNull((T)this.tail)).next = node;
      node.previous = this.tail;
      this.tail = node;
      KeyList<K, V> keyList = this.keyToKeyList.get(key);
      if (keyList == null) {
        this.keyToKeyList.put(key, keyList = new KeyList<>(node));
        this.modCount++;
      } else {
        keyList.count++;
        Node<K, V> keyTail = keyList.tail;
        keyTail.nextSibling = node;
        node.previousSibling = keyTail;
        keyList.tail = node;
      } 
    } else {
      KeyList<K, V> keyList = Objects.<KeyList<K, V>>requireNonNull(this.keyToKeyList.get(key));
      keyList.count++;
      node.previous = nextSibling.previous;
      node.previousSibling = nextSibling.previousSibling;
      node.next = nextSibling;
      node.nextSibling = nextSibling;
      if (nextSibling.previousSibling == null) {
        keyList.head = node;
      } else {
        nextSibling.previousSibling.nextSibling = node;
      } 
      if (nextSibling.previous == null) {
        this.head = node;
      } else {
        nextSibling.previous.next = node;
      } 
      nextSibling.previous = node;
      nextSibling.previousSibling = node;
    } 
    this.size++;
    return node;
  }
  
  private void removeNode(Node<K, V> node) {
    if (node.previous != null) {
      node.previous.next = node.next;
    } else {
      this.head = node.next;
    } 
    if (node.next != null) {
      node.next.previous = node.previous;
    } else {
      this.tail = node.previous;
    } 
    if (node.previousSibling == null && node.nextSibling == null) {
      KeyList<K, V> keyList = Objects.<KeyList<K, V>>requireNonNull(this.keyToKeyList.remove(node.key));
      keyList.count = 0;
      this.modCount++;
    } else {
      KeyList<K, V> keyList = Objects.<KeyList<K, V>>requireNonNull(this.keyToKeyList.get(node.key));
      keyList.count--;
      if (node.previousSibling == null) {
        keyList.head = Objects.<Node<K, V>>requireNonNull(node.nextSibling);
      } else {
        node.previousSibling.nextSibling = node.nextSibling;
      } 
      if (node.nextSibling == null) {
        keyList.tail = Objects.<Node<K, V>>requireNonNull(node.previousSibling);
      } else {
        node.nextSibling.previousSibling = node.previousSibling;
      } 
    } 
    this.size--;
  }
  
  private void removeAllNodes(@ParametricNullness K key) {
    Iterators.clear(new ValueForKeyIterator(key));
  }
  
  private class NodeIterator implements ListIterator<Map.Entry<K, V>> {
    int nextIndex;
    
    @CheckForNull
    LinkedListMultimap.Node<K, V> next;
    
    @CheckForNull
    LinkedListMultimap.Node<K, V> current;
    
    @CheckForNull
    LinkedListMultimap.Node<K, V> previous;
    
    int expectedModCount = LinkedListMultimap.this.modCount;
    
    private void checkForConcurrentModification() {
      if (LinkedListMultimap.this.modCount != this.expectedModCount)
        throw new ConcurrentModificationException(); 
    }
    
    public boolean hasNext() {
      checkForConcurrentModification();
      return (this.next != null);
    }
    
    @CanIgnoreReturnValue
    public LinkedListMultimap.Node<K, V> next() {
      checkForConcurrentModification();
      if (this.next == null)
        throw new NoSuchElementException(); 
      this.previous = this.current = this.next;
      this.next = this.next.next;
      this.nextIndex++;
      return this.current;
    }
    
    public void remove() {
      checkForConcurrentModification();
      Preconditions.checkState((this.current != null), "no calls to next() since the last call to remove()");
      if (this.current != this.next) {
        this.previous = this.current.previous;
        this.nextIndex--;
      } else {
        this.next = this.current.next;
      } 
      LinkedListMultimap.this.removeNode(this.current);
      this.current = null;
      this.expectedModCount = LinkedListMultimap.this.modCount;
    }
    
    public boolean hasPrevious() {
      checkForConcurrentModification();
      return (this.previous != null);
    }
    
    @CanIgnoreReturnValue
    public LinkedListMultimap.Node<K, V> previous() {
      checkForConcurrentModification();
      if (this.previous == null)
        throw new NoSuchElementException(); 
      this.next = this.current = this.previous;
      this.previous = this.previous.previous;
      this.nextIndex--;
      return this.current;
    }
    
    public int nextIndex() {
      return this.nextIndex;
    }
    
    public int previousIndex() {
      return this.nextIndex - 1;
    }
    
    public void set(Map.Entry<K, V> e) {
      throw new UnsupportedOperationException();
    }
    
    public void add(Map.Entry<K, V> e) {
      throw new UnsupportedOperationException();
    }
    
    void setValue(@ParametricNullness V value) {
      Preconditions.checkState((this.current != null));
      this.current.value = value;
    }
    
    NodeIterator(int index) {
      int size = LinkedListMultimap.this.size();
      Preconditions.checkPositionIndex(index, size);
      if (index >= size / 2) {
        this.previous = LinkedListMultimap.this.tail;
        this.nextIndex = size;
        while (index++ < size)
          previous(); 
      } else {
        this.next = LinkedListMultimap.this.head;
        while (index-- > 0)
          next(); 
      } 
      this.current = null;
    }
  }
  
  private class DistinctKeyIterator implements Iterator<K> {
    final Set<K> seenKeys = Sets.newHashSetWithExpectedSize(LinkedListMultimap.this.keySet().size());
    
    @CheckForNull
    LinkedListMultimap.Node<K, V> next = LinkedListMultimap.this.head;
    
    @CheckForNull
    LinkedListMultimap.Node<K, V> current;
    
    int expectedModCount = LinkedListMultimap.this.modCount;
    
    private void checkForConcurrentModification() {
      if (LinkedListMultimap.this.modCount != this.expectedModCount)
        throw new ConcurrentModificationException(); 
    }
    
    public boolean hasNext() {
      checkForConcurrentModification();
      return (this.next != null);
    }
    
    @ParametricNullness
    public K next() {
      checkForConcurrentModification();
      if (this.next == null)
        throw new NoSuchElementException(); 
      this.current = this.next;
      this.seenKeys.add(this.current.key);
      do {
        this.next = this.next.next;
      } while (this.next != null && !this.seenKeys.add(this.next.key));
      return this.current.key;
    }
    
    public void remove() {
      checkForConcurrentModification();
      Preconditions.checkState((this.current != null), "no calls to next() since the last call to remove()");
      LinkedListMultimap.this.removeAllNodes(this.current.key);
      this.current = null;
      this.expectedModCount = LinkedListMultimap.this.modCount;
    }
    
    private DistinctKeyIterator() {}
  }
  
  private class ValueForKeyIterator implements ListIterator<V> {
    @ParametricNullness
    final K key;
    
    int nextIndex;
    
    @CheckForNull
    LinkedListMultimap.Node<K, V> next;
    
    @CheckForNull
    LinkedListMultimap.Node<K, V> current;
    
    @CheckForNull
    LinkedListMultimap.Node<K, V> previous;
    
    ValueForKeyIterator(K key) {
      this.key = key;
      LinkedListMultimap.KeyList<K, V> keyList = (LinkedListMultimap.KeyList<K, V>)LinkedListMultimap.this.keyToKeyList.get(key);
      this.next = (keyList == null) ? null : keyList.head;
    }
    
    public ValueForKeyIterator(K key, int index) {
      LinkedListMultimap.KeyList<K, V> keyList = (LinkedListMultimap.KeyList<K, V>)LinkedListMultimap.this.keyToKeyList.get(key);
      int size = (keyList == null) ? 0 : keyList.count;
      Preconditions.checkPositionIndex(index, size);
      if (index >= size / 2) {
        this.previous = (keyList == null) ? null : keyList.tail;
        this.nextIndex = size;
        while (index++ < size)
          previous(); 
      } else {
        this.next = (keyList == null) ? null : keyList.head;
        while (index-- > 0)
          next(); 
      } 
      this.key = key;
      this.current = null;
    }
    
    public boolean hasNext() {
      return (this.next != null);
    }
    
    @ParametricNullness
    @CanIgnoreReturnValue
    public V next() {
      if (this.next == null)
        throw new NoSuchElementException(); 
      this.previous = this.current = this.next;
      this.next = this.next.nextSibling;
      this.nextIndex++;
      return this.current.value;
    }
    
    public boolean hasPrevious() {
      return (this.previous != null);
    }
    
    @ParametricNullness
    @CanIgnoreReturnValue
    public V previous() {
      if (this.previous == null)
        throw new NoSuchElementException(); 
      this.next = this.current = this.previous;
      this.previous = this.previous.previousSibling;
      this.nextIndex--;
      return this.current.value;
    }
    
    public int nextIndex() {
      return this.nextIndex;
    }
    
    public int previousIndex() {
      return this.nextIndex - 1;
    }
    
    public void remove() {
      Preconditions.checkState((this.current != null), "no calls to next() since the last call to remove()");
      if (this.current != this.next) {
        this.previous = this.current.previousSibling;
        this.nextIndex--;
      } else {
        this.next = this.current.nextSibling;
      } 
      LinkedListMultimap.this.removeNode(this.current);
      this.current = null;
    }
    
    public void set(@ParametricNullness V value) {
      Preconditions.checkState((this.current != null));
      this.current.value = value;
    }
    
    public void add(@ParametricNullness V value) {
      this.previous = LinkedListMultimap.this.addNode(this.key, value, this.next);
      this.nextIndex++;
      this.current = null;
    }
  }
  
  public int size() {
    return this.size;
  }
  
  public boolean isEmpty() {
    return (this.head == null);
  }
  
  public boolean containsKey(@CheckForNull Object key) {
    return this.keyToKeyList.containsKey(key);
  }
  
  public boolean containsValue(@CheckForNull Object value) {
    return values().contains(value);
  }
  
  @CanIgnoreReturnValue
  public boolean put(@ParametricNullness K key, @ParametricNullness V value) {
    addNode(key, value, null);
    return true;
  }
  
  @CanIgnoreReturnValue
  public List<V> replaceValues(@ParametricNullness K key, Iterable<? extends V> values) {
    List<V> oldValues = getCopy(key);
    ListIterator<V> keyValues = new ValueForKeyIterator(key);
    Iterator<? extends V> newValues = values.iterator();
    while (keyValues.hasNext() && newValues.hasNext()) {
      keyValues.next();
      keyValues.set(newValues.next());
    } 
    while (keyValues.hasNext()) {
      keyValues.next();
      keyValues.remove();
    } 
    while (newValues.hasNext())
      keyValues.add(newValues.next()); 
    return oldValues;
  }
  
  private List<V> getCopy(@ParametricNullness K key) {
    return Collections.unmodifiableList(Lists.newArrayList(new ValueForKeyIterator(key)));
  }
  
  @CanIgnoreReturnValue
  public List<V> removeAll(@CheckForNull Object key) {
    K castKey = (K)key;
    List<V> oldValues = getCopy(castKey);
    removeAllNodes(castKey);
    return oldValues;
  }
  
  public void clear() {
    this.head = null;
    this.tail = null;
    this.keyToKeyList.clear();
    this.size = 0;
    this.modCount++;
  }
  
  public List<V> get(@ParametricNullness final K key) {
    return new AbstractSequentialList<V>() {
        public int size() {
          LinkedListMultimap.KeyList<K, V> keyList = (LinkedListMultimap.KeyList<K, V>)LinkedListMultimap.this.keyToKeyList.get(key);
          return (keyList == null) ? 0 : keyList.count;
        }
        
        public ListIterator<V> listIterator(int index) {
          return new LinkedListMultimap.ValueForKeyIterator((K)key, index);
        }
      };
  }
  
  Set<K> createKeySet() {
    class KeySetImpl extends Sets.ImprovedAbstractSet<K> {
      public int size() {
        return LinkedListMultimap.this.keyToKeyList.size();
      }
      
      public Iterator<K> iterator() {
        return new LinkedListMultimap.DistinctKeyIterator();
      }
      
      public boolean contains(@CheckForNull Object key) {
        return LinkedListMultimap.this.containsKey(key);
      }
      
      public boolean remove(@CheckForNull Object o) {
        return !LinkedListMultimap.this.removeAll(o).isEmpty();
      }
    };
    return new KeySetImpl();
  }
  
  Multiset<K> createKeys() {
    return new Multimaps.Keys<>(this);
  }
  
  public List<V> values() {
    return (List<V>)super.values();
  }
  
  List<V> createValues() {
    class ValuesImpl extends AbstractSequentialList<V> {
      public int size() {
        return LinkedListMultimap.this.size;
      }
      
      public ListIterator<V> listIterator(int index) {
        final LinkedListMultimap<K, V>.NodeIterator nodeItr = new LinkedListMultimap.NodeIterator(index);
        return new TransformedListIterator<Map.Entry<K, V>, V>(this, nodeItr) {
            @ParametricNullness
            V transform(Map.Entry<K, V> entry) {
              return entry.getValue();
            }
            
            public void set(@ParametricNullness V value) {
              nodeItr.setValue(value);
            }
          };
      }
    };
    return new ValuesImpl();
  }
  
  public List<Map.Entry<K, V>> entries() {
    return (List<Map.Entry<K, V>>)super.entries();
  }
  
  List<Map.Entry<K, V>> createEntries() {
    class EntriesImpl extends AbstractSequentialList<Map.Entry<K, V>> {
      public int size() {
        return LinkedListMultimap.this.size;
      }
      
      public ListIterator<Map.Entry<K, V>> listIterator(int index) {
        return new LinkedListMultimap.NodeIterator(index);
      }
      
      public void forEach(Consumer<? super Map.Entry<K, V>> action) {
        Preconditions.checkNotNull(action);
        for (LinkedListMultimap.Node<K, V> node = LinkedListMultimap.this.head; node != null; node = node.next)
          action.accept(node); 
      }
    };
    return new EntriesImpl();
  }
  
  Iterator<Map.Entry<K, V>> entryIterator() {
    throw new AssertionError("should never be called");
  }
  
  Map<K, Collection<V>> createAsMap() {
    return new Multimaps.AsMap<>(this);
  }
  
  @GwtIncompatible
  @J2ktIncompatible
  private void writeObject(ObjectOutputStream stream) throws IOException {
    stream.defaultWriteObject();
    stream.writeInt(size());
    for (Map.Entry<K, V> entry : entries()) {
      stream.writeObject(entry.getKey());
      stream.writeObject(entry.getValue());
    } 
  }
  
  @GwtIncompatible
  @J2ktIncompatible
  private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
    stream.defaultReadObject();
    this.keyToKeyList = Maps.newLinkedHashMap();
    int size = stream.readInt();
    for (int i = 0; i < size; i++) {
      K key = (K)stream.readObject();
      V value = (V)stream.readObject();
      put(key, value);
    } 
  }
}
