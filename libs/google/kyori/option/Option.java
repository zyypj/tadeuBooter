package me.syncwrld.booter.libs.google.kyori.option;

import me.syncwrld.booter.libs.jtann.ApiStatus.NonExtendable;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

@NonExtendable
public interface Option<V> {
  static Option<Boolean> booleanOption(String id, boolean defaultValue) {
    return OptionImpl.option(id, Boolean.class, Boolean.valueOf(defaultValue));
  }
  
  static <E extends Enum<E>> Option<E> enumOption(String id, Class<E> enumClazz, E defaultValue) {
    return OptionImpl.option(id, enumClazz, defaultValue);
  }
  
  @NotNull
  String id();
  
  @NotNull
  Class<V> type();
  
  @Nullable
  V defaultValue();
}
