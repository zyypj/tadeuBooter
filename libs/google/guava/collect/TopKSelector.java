package me.syncwrld.booter.libs.google.guava.collect;

import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.guava.math.IntMath;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
final class TopKSelector<T> {
  private final int k;
  
  private final Comparator<? super T> comparator;
  
  private final T[] buffer;
  
  private int bufferSize;
  
  @CheckForNull
  private T threshold;
  
  public static <T extends Comparable<? super T>> TopKSelector<T> least(int k) {
    return least(k, Ordering.natural());
  }
  
  public static <T> TopKSelector<T> least(int k, Comparator<? super T> comparator) {
    return new TopKSelector<>(comparator, k);
  }
  
  public static <T extends Comparable<? super T>> TopKSelector<T> greatest(int k) {
    return greatest(k, Ordering.natural());
  }
  
  public static <T> TopKSelector<T> greatest(int k, Comparator<? super T> comparator) {
    return new TopKSelector<>(Ordering.<T>from(comparator).reverse(), k);
  }
  
  private TopKSelector(Comparator<? super T> comparator, int k) {
    this.comparator = (Comparator<? super T>)Preconditions.checkNotNull(comparator, "comparator");
    this.k = k;
    Preconditions.checkArgument((k >= 0), "k (%s) must be >= 0", k);
    Preconditions.checkArgument((k <= 1073741823), "k (%s) must be <= Integer.MAX_VALUE / 2", k);
    this.buffer = (T[])new Object[IntMath.checkedMultiply(k, 2)];
    this.bufferSize = 0;
    this.threshold = null;
  }
  
  public void offer(@ParametricNullness T elem) {
    if (this.k == 0)
      return; 
    if (this.bufferSize == 0) {
      this.buffer[0] = elem;
      this.threshold = elem;
      this.bufferSize = 1;
    } else if (this.bufferSize < this.k) {
      this.buffer[this.bufferSize++] = elem;
      if (this.comparator.compare(elem, NullnessCasts.uncheckedCastNullableTToT(this.threshold)) > 0)
        this.threshold = elem; 
    } else if (this.comparator.compare(elem, NullnessCasts.uncheckedCastNullableTToT(this.threshold)) < 0) {
      this.buffer[this.bufferSize++] = elem;
      if (this.bufferSize == 2 * this.k)
        trim(); 
    } 
  }
  
  private void trim() {
    int left = 0;
    int right = 2 * this.k - 1;
    int minThresholdPosition = 0;
    int iterations = 0;
    int maxIterations = IntMath.log2(right - left, RoundingMode.CEILING) * 3;
    while (left < right) {
      int pivotIndex = left + right + 1 >>> 1;
      int pivotNewIndex = partition(left, right, pivotIndex);
      if (pivotNewIndex > this.k) {
        right = pivotNewIndex - 1;
      } else if (pivotNewIndex < this.k) {
        left = Math.max(pivotNewIndex, left + 1);
        minThresholdPosition = pivotNewIndex;
      } else {
        break;
      } 
      iterations++;
      if (iterations >= maxIterations) {
        T[] castBuffer = this.buffer;
        Arrays.sort(castBuffer, left, right + 1, this.comparator);
        break;
      } 
    } 
    this.bufferSize = this.k;
    this.threshold = NullnessCasts.uncheckedCastNullableTToT(this.buffer[minThresholdPosition]);
    for (int i = minThresholdPosition + 1; i < this.k; i++) {
      if (this.comparator.compare(
          NullnessCasts.uncheckedCastNullableTToT(this.buffer[i]), NullnessCasts.uncheckedCastNullableTToT(this.threshold)) > 0)
        this.threshold = this.buffer[i]; 
    } 
  }
  
  private int partition(int left, int right, int pivotIndex) {
    T pivotValue = NullnessCasts.uncheckedCastNullableTToT(this.buffer[pivotIndex]);
    this.buffer[pivotIndex] = this.buffer[right];
    int pivotNewIndex = left;
    for (int i = left; i < right; i++) {
      if (this.comparator.compare(NullnessCasts.uncheckedCastNullableTToT(this.buffer[i]), pivotValue) < 0) {
        swap(pivotNewIndex, i);
        pivotNewIndex++;
      } 
    } 
    this.buffer[right] = this.buffer[pivotNewIndex];
    this.buffer[pivotNewIndex] = pivotValue;
    return pivotNewIndex;
  }
  
  private void swap(int i, int j) {
    T tmp = this.buffer[i];
    this.buffer[i] = this.buffer[j];
    this.buffer[j] = tmp;
  }
  
  TopKSelector<T> combine(TopKSelector<T> other) {
    for (int i = 0; i < other.bufferSize; i++)
      offer(NullnessCasts.uncheckedCastNullableTToT(other.buffer[i])); 
    return this;
  }
  
  public void offerAll(Iterable<? extends T> elements) {
    offerAll(elements.iterator());
  }
  
  public void offerAll(Iterator<? extends T> elements) {
    while (elements.hasNext())
      offer(elements.next()); 
  }
  
  public List<T> topK() {
    T[] castBuffer = this.buffer;
    Arrays.sort(castBuffer, 0, this.bufferSize, this.comparator);
    if (this.bufferSize > this.k) {
      Arrays.fill((Object[])this.buffer, this.k, this.buffer.length, (Object)null);
      this.bufferSize = this.k;
      this.threshold = this.buffer[this.k - 1];
    } 
    T[] topK = Arrays.copyOf(castBuffer, this.bufferSize);
    return Collections.unmodifiableList(Arrays.asList(topK));
  }
}
