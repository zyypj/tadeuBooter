package me.syncwrld.booter.libs.unnamed.inject.multibinding;

import java.util.Map;

public interface MapCreator {
  <K, V> Map<K, V> create();
}
