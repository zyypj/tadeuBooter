package me.syncwrld.booter.libs.google.guava.util.concurrent;

import java.util.Collections;
import java.util.List;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.collect.ImmutableCollection;
import me.syncwrld.booter.libs.google.guava.collect.Lists;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible(emulated = true)
abstract class CollectionFuture<V, C> extends AggregateFuture<V, C> {
  @CheckForNull
  private List<Present<V>> values;
  
  CollectionFuture(ImmutableCollection<? extends ListenableFuture<? extends V>> futures, boolean allMustSucceed) {
    super(futures, allMustSucceed, true);
    List<Present<V>> values = futures.isEmpty() ? Collections.<Present<V>>emptyList() : Lists.newArrayListWithCapacity(futures.size());
    for (int i = 0; i < futures.size(); i++)
      values.add(null); 
    this.values = values;
  }
  
  final void collectOneValue(int index, @ParametricNullness V returnValue) {
    List<Present<V>> localValues = this.values;
    if (localValues != null)
      localValues.set(index, new Present<>(returnValue)); 
  }
  
  final void handleAllCompleted() {
    List<Present<V>> localValues = this.values;
    if (localValues != null)
      set(combine(localValues)); 
  }
  
  void releaseResources(AggregateFuture.ReleaseResourcesReason reason) {
    super.releaseResources(reason);
    this.values = null;
  }
  
  abstract C combine(List<Present<V>> paramList);
  
  static final class ListFuture<V> extends CollectionFuture<V, List<V>> {
    ListFuture(ImmutableCollection<? extends ListenableFuture<? extends V>> futures, boolean allMustSucceed) {
      super(futures, allMustSucceed);
      init();
    }
    
    public List<V> combine(List<CollectionFuture.Present<V>> values) {
      List<V> result = Lists.newArrayListWithCapacity(values.size());
      for (CollectionFuture.Present<V> element : values)
        result.add((element != null) ? element.value : null); 
      return Collections.unmodifiableList(result);
    }
  }
  
  private static final class Present<V> {
    @ParametricNullness
    final V value;
    
    Present(@ParametricNullness V value) {
      this.value = value;
    }
  }
}
