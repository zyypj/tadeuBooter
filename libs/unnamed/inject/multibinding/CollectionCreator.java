package me.syncwrld.booter.libs.unnamed.inject.multibinding;

import java.util.Collection;

public interface CollectionCreator {
  <E> Collection<E> create();
}
