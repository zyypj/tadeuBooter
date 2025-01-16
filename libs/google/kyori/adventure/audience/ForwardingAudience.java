package me.syncwrld.booter.libs.google.kyori.adventure.audience;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import me.syncwrld.booter.libs.google.kyori.adventure.bossbar.BossBar;
import me.syncwrld.booter.libs.google.kyori.adventure.chat.ChatType;
import me.syncwrld.booter.libs.google.kyori.adventure.chat.SignedMessage;
import me.syncwrld.booter.libs.google.kyori.adventure.identity.Identified;
import me.syncwrld.booter.libs.google.kyori.adventure.identity.Identity;
import me.syncwrld.booter.libs.google.kyori.adventure.inventory.Book;
import me.syncwrld.booter.libs.google.kyori.adventure.pointer.Pointer;
import me.syncwrld.booter.libs.google.kyori.adventure.pointer.Pointers;
import me.syncwrld.booter.libs.google.kyori.adventure.resource.ResourcePackRequest;
import me.syncwrld.booter.libs.google.kyori.adventure.sound.Sound;
import me.syncwrld.booter.libs.google.kyori.adventure.sound.SoundStop;
import me.syncwrld.booter.libs.google.kyori.adventure.text.Component;
import me.syncwrld.booter.libs.google.kyori.adventure.title.TitlePart;
import me.syncwrld.booter.libs.jtann.ApiStatus.OverrideOnly;
import me.syncwrld.booter.libs.jtann.Contract;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

@FunctionalInterface
public interface ForwardingAudience extends Audience {
  @OverrideOnly
  @NotNull
  Iterable<? extends Audience> audiences();
  
  @NotNull
  default Pointers pointers() {
    return Pointers.empty();
  }
  
  @NotNull
  default Audience filterAudience(@NotNull Predicate<? super Audience> filter) {
    List<Audience> audiences = null;
    for (Audience audience : audiences()) {
      if (filter.test(audience)) {
        Audience filtered = audience.filterAudience(filter);
        if (filtered != Audience.empty()) {
          if (audiences == null)
            audiences = new ArrayList<>(); 
          audiences.add(filtered);
        } 
      } 
    } 
    return (audiences != null) ? 
      Audience.audience(audiences) : 
      Audience.empty();
  }
  
  default void forEachAudience(@NotNull Consumer<? super Audience> action) {
    for (Audience audience : audiences())
      audience.forEachAudience(action); 
  }
  
  default void sendMessage(@NotNull Component message) {
    for (Audience audience : audiences())
      audience.sendMessage(message); 
  }
  
  default void sendMessage(@NotNull Component message, ChatType.Bound boundChatType) {
    for (Audience audience : audiences())
      audience.sendMessage(message, boundChatType); 
  }
  
  default void sendMessage(@NotNull SignedMessage signedMessage, ChatType.Bound boundChatType) {
    for (Audience audience : audiences())
      audience.sendMessage(signedMessage, boundChatType); 
  }
  
  default void deleteMessage(SignedMessage.Signature signature) {
    for (Audience audience : audiences())
      audience.deleteMessage(signature); 
  }
  
  @Deprecated
  default void sendMessage(@NotNull Identified source, @NotNull Component message, @NotNull MessageType type) {
    for (Audience audience : audiences())
      audience.sendMessage(source, message, type); 
  }
  
  @Deprecated
  default void sendMessage(@NotNull Identity source, @NotNull Component message, @NotNull MessageType type) {
    for (Audience audience : audiences())
      audience.sendMessage(source, message, type); 
  }
  
  default void sendActionBar(@NotNull Component message) {
    for (Audience audience : audiences())
      audience.sendActionBar(message); 
  }
  
  default void sendPlayerListHeader(@NotNull Component header) {
    for (Audience audience : audiences())
      audience.sendPlayerListHeader(header); 
  }
  
  default void sendPlayerListFooter(@NotNull Component footer) {
    for (Audience audience : audiences())
      audience.sendPlayerListFooter(footer); 
  }
  
  default void sendPlayerListHeaderAndFooter(@NotNull Component header, @NotNull Component footer) {
    for (Audience audience : audiences())
      audience.sendPlayerListHeaderAndFooter(header, footer); 
  }
  
  default <T> void sendTitlePart(@NotNull TitlePart<T> part, @NotNull T value) {
    for (Audience audience : audiences())
      audience.sendTitlePart(part, value); 
  }
  
  default void clearTitle() {
    for (Audience audience : audiences())
      audience.clearTitle(); 
  }
  
  default void resetTitle() {
    for (Audience audience : audiences())
      audience.resetTitle(); 
  }
  
  default void showBossBar(@NotNull BossBar bar) {
    for (Audience audience : audiences())
      audience.showBossBar(bar); 
  }
  
  default void hideBossBar(@NotNull BossBar bar) {
    for (Audience audience : audiences())
      audience.hideBossBar(bar); 
  }
  
  default void playSound(@NotNull Sound sound) {
    for (Audience audience : audiences())
      audience.playSound(sound); 
  }
  
  default void playSound(@NotNull Sound sound, double x, double y, double z) {
    for (Audience audience : audiences())
      audience.playSound(sound, x, y, z); 
  }
  
  default void playSound(@NotNull Sound sound, Sound.Emitter emitter) {
    for (Audience audience : audiences())
      audience.playSound(sound, emitter); 
  }
  
  default void stopSound(@NotNull SoundStop stop) {
    for (Audience audience : audiences())
      audience.stopSound(stop); 
  }
  
  default void openBook(@NotNull Book book) {
    for (Audience audience : audiences())
      audience.openBook(book); 
  }
  
  default void sendResourcePacks(@NotNull ResourcePackRequest request) {
    for (Audience audience : audiences())
      audience.sendResourcePacks(request); 
  }
  
  default void removeResourcePacks(@NotNull Iterable<UUID> ids) {
    for (Audience audience : audiences())
      audience.removeResourcePacks(ids); 
  }
  
  void removeResourcePacks(@NotNull UUID id, @NotNull UUID... others) {
    for (Audience audience : audiences())
      audience.removeResourcePacks(id, others); 
  }
  
  default void clearResourcePacks() {
    for (Audience audience : audiences())
      audience.clearResourcePacks(); 
  }
  
  public static interface Single extends ForwardingAudience {
    @OverrideOnly
    @NotNull
    Audience audience();
    
    @Deprecated
    @NotNull
    default Iterable<? extends Audience> audiences() {
      return Collections.singleton(audience());
    }
    
    @NotNull
    default <T> Optional<T> get(@NotNull Pointer<T> pointer) {
      return audience().get(pointer);
    }
    
    @Contract("_, null -> null; _, !null -> !null")
    @Nullable
    default <T> T getOrDefault(@NotNull Pointer<T> pointer, @Nullable T defaultValue) {
      return (T)audience().getOrDefault(pointer, defaultValue);
    }
    
    default <T> T getOrDefaultFrom(@NotNull Pointer<T> pointer, @NotNull Supplier<? extends T> defaultValue) {
      return (T)audience().getOrDefaultFrom(pointer, defaultValue);
    }
    
    @NotNull
    default Audience filterAudience(@NotNull Predicate<? super Audience> filter) {
      Audience audience = audience();
      return filter.test(audience) ? 
        this : 
        Audience.empty();
    }
    
    default void forEachAudience(@NotNull Consumer<? super Audience> action) {
      audience().forEachAudience(action);
    }
    
    @NotNull
    default Pointers pointers() {
      return audience().pointers();
    }
    
    default void sendMessage(@NotNull Component message) {
      audience().sendMessage(message);
    }
    
    default void sendMessage(@NotNull Component message, ChatType.Bound boundChatType) {
      audience().sendMessage(message, boundChatType);
    }
    
    default void sendMessage(@NotNull SignedMessage signedMessage, ChatType.Bound boundChatType) {
      audience().sendMessage(signedMessage, boundChatType);
    }
    
    default void deleteMessage(SignedMessage.Signature signature) {
      audience().deleteMessage(signature);
    }
    
    @Deprecated
    default void sendMessage(@NotNull Identified source, @NotNull Component message, @NotNull MessageType type) {
      audience().sendMessage(source, message, type);
    }
    
    @Deprecated
    default void sendMessage(@NotNull Identity source, @NotNull Component message, @NotNull MessageType type) {
      audience().sendMessage(source, message, type);
    }
    
    default void sendActionBar(@NotNull Component message) {
      audience().sendActionBar(message);
    }
    
    default void sendPlayerListHeader(@NotNull Component header) {
      audience().sendPlayerListHeader(header);
    }
    
    default void sendPlayerListFooter(@NotNull Component footer) {
      audience().sendPlayerListFooter(footer);
    }
    
    default void sendPlayerListHeaderAndFooter(@NotNull Component header, @NotNull Component footer) {
      audience().sendPlayerListHeaderAndFooter(header, footer);
    }
    
    default <T> void sendTitlePart(@NotNull TitlePart<T> part, @NotNull T value) {
      audience().sendTitlePart(part, value);
    }
    
    default void clearTitle() {
      audience().clearTitle();
    }
    
    default void resetTitle() {
      audience().resetTitle();
    }
    
    default void showBossBar(@NotNull BossBar bar) {
      audience().showBossBar(bar);
    }
    
    default void hideBossBar(@NotNull BossBar bar) {
      audience().hideBossBar(bar);
    }
    
    default void playSound(@NotNull Sound sound) {
      audience().playSound(sound);
    }
    
    default void playSound(@NotNull Sound sound, double x, double y, double z) {
      audience().playSound(sound, x, y, z);
    }
    
    default void playSound(@NotNull Sound sound, Sound.Emitter emitter) {
      audience().playSound(sound, emitter);
    }
    
    default void stopSound(@NotNull SoundStop stop) {
      audience().stopSound(stop);
    }
    
    default void openBook(@NotNull Book book) {
      audience().openBook(book);
    }
    
    default void sendResourcePacks(@NotNull ResourcePackRequest request) {
      audience().sendResourcePacks(request.callback(Audiences.unwrapCallback(this, audience(), request.callback())));
    }
    
    default void removeResourcePacks(@NotNull Iterable<UUID> ids) {
      audience().removeResourcePacks(ids);
    }
    
    void removeResourcePacks(@NotNull UUID id, @NotNull UUID... others) {
      audience().removeResourcePacks(id, others);
    }
    
    default void clearResourcePacks() {
      audience().clearResourcePacks();
    }
  }
}
