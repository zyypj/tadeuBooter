package me.syncwrld.booter.libs.google.kyori.adventure.util;

import java.util.ServiceLoader;

final class Services0 {
  static <S> ServiceLoader<S> loader(Class<S> type) {
    return ServiceLoader.load(type, type.getClassLoader());
  }
}
