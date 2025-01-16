package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Set;
import java.util.SortedSet;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
interface SortedMultisetBridge<E> extends Multiset<E> {
  SortedSet<E> elementSet();
}
