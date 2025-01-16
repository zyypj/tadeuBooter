package me.syncwrld.booter.libs.google.kyori.adventure.identity;

import java.util.Locale;
import java.util.UUID;
import java.util.stream.Stream;
import me.syncwrld.booter.libs.google.kyori.adventure.key.Key;
import me.syncwrld.booter.libs.google.kyori.adventure.pointer.Pointer;
import me.syncwrld.booter.libs.google.kyori.adventure.text.Component;
import me.syncwrld.booter.libs.google.kyori.examination.Examinable;
import me.syncwrld.booter.libs.google.kyori.examination.ExaminableProperty;
import me.syncwrld.booter.libs.jtann.NotNull;

public interface Identity extends Examinable, Identified {
  public static final Pointer<String> NAME = Pointer.pointer(String.class, Key.key("adventure", "name"));
  
  public static final Pointer<UUID> UUID = Pointer.pointer(UUID.class, Key.key("adventure", "uuid"));
  
  public static final Pointer<Component> DISPLAY_NAME = Pointer.pointer(Component.class, Key.key("adventure", "display_name"));
  
  public static final Pointer<Locale> LOCALE = Pointer.pointer(Locale.class, Key.key("adventure", "locale"));
  
  @NotNull
  static Identity nil() {
    return NilIdentity.INSTANCE;
  }
  
  @NotNull
  static Identity identity(@NotNull UUID uuid) {
    if (uuid.equals(NilIdentity.NIL_UUID))
      return NilIdentity.INSTANCE; 
    return new IdentityImpl(uuid);
  }
  
  @NotNull
  UUID uuid();
  
  @NotNull
  default Identity identity() {
    return this;
  }
  
  @NotNull
  default Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(ExaminableProperty.of("uuid", uuid()));
  }
}
