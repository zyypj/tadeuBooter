package me.syncwrld.booter.libs.google.kyori.adventure.text;

import java.util.Objects;
import java.util.stream.Stream;
import me.syncwrld.booter.libs.google.kyori.examination.ExaminableProperty;
import me.syncwrld.booter.libs.jtann.Contract;
import me.syncwrld.booter.libs.jtann.NotNull;

public interface KeybindComponent extends BuildableComponent<KeybindComponent, KeybindComponent.Builder>, ScopedComponent<KeybindComponent> {
  @NotNull
  String keybind();
  
  @Contract(pure = true)
  @NotNull
  KeybindComponent keybind(@NotNull String paramString);
  
  @Contract(pure = true)
  @NotNull
  default KeybindComponent keybind(@NotNull KeybindLike keybind) {
    return keybind(((KeybindLike)Objects.<KeybindLike>requireNonNull(keybind, "keybind")).asKeybind());
  }
  
  @NotNull
  default Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.concat(
        Stream.of(
          ExaminableProperty.of("keybind", keybind())), super
        
        .examinableProperties());
  }
  
  public static interface Builder extends ComponentBuilder<KeybindComponent, Builder> {
    @Contract("_ -> this")
    @NotNull
    Builder keybind(@NotNull String param1String);
    
    @Contract(pure = true)
    @NotNull
    default Builder keybind(@NotNull KeybindComponent.KeybindLike keybind) {
      return keybind(((KeybindComponent.KeybindLike)Objects.<KeybindComponent.KeybindLike>requireNonNull(keybind, "keybind")).asKeybind());
    }
  }
  
  public static interface KeybindLike {
    @NotNull
    String asKeybind();
  }
}
