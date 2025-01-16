package me.syncwrld.booter.libs.google.kyori.adventure.audience;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import me.syncwrld.booter.libs.google.kyori.adventure.chat.ChatType;
import me.syncwrld.booter.libs.google.kyori.adventure.chat.SignedMessage;
import me.syncwrld.booter.libs.google.kyori.adventure.identity.Identified;
import me.syncwrld.booter.libs.google.kyori.adventure.identity.Identity;
import me.syncwrld.booter.libs.google.kyori.adventure.inventory.Book;
import me.syncwrld.booter.libs.google.kyori.adventure.pointer.Pointer;
import me.syncwrld.booter.libs.google.kyori.adventure.resource.ResourcePackInfoLike;
import me.syncwrld.booter.libs.google.kyori.adventure.resource.ResourcePackRequest;
import me.syncwrld.booter.libs.google.kyori.adventure.text.Component;
import me.syncwrld.booter.libs.google.kyori.adventure.text.ComponentLike;
import me.syncwrld.booter.libs.jtann.Contract;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

final class EmptyAudience implements Audience {
  static final EmptyAudience INSTANCE = new EmptyAudience();
  
  @NotNull
  public <T> Optional<T> get(@NotNull Pointer<T> pointer) {
    return Optional.empty();
  }
  
  @Contract("_, null -> null; _, !null -> !null")
  @Nullable
  public <T> T getOrDefault(@NotNull Pointer<T> pointer, @Nullable T defaultValue) {
    return defaultValue;
  }
  
  public <T> T getOrDefaultFrom(@NotNull Pointer<T> pointer, @NotNull Supplier<? extends T> defaultValue) {
    return defaultValue.get();
  }
  
  @NotNull
  public Audience filterAudience(@NotNull Predicate<? super Audience> filter) {
    return this;
  }
  
  public void forEachAudience(@NotNull Consumer<? super Audience> action) {}
  
  public void sendMessage(@NotNull ComponentLike message) {}
  
  public void sendMessage(@NotNull Component message) {}
  
  @Deprecated
  public void sendMessage(@NotNull Identified source, @NotNull Component message, @NotNull MessageType type) {}
  
  @Deprecated
  public void sendMessage(@NotNull Identity source, @NotNull Component message, @NotNull MessageType type) {}
  
  public void sendMessage(@NotNull Component message, ChatType.Bound boundChatType) {}
  
  public void sendMessage(@NotNull SignedMessage signedMessage, ChatType.Bound boundChatType) {}
  
  public void deleteMessage(SignedMessage.Signature signature) {}
  
  public void sendActionBar(@NotNull ComponentLike message) {}
  
  public void sendPlayerListHeader(@NotNull ComponentLike header) {}
  
  public void sendPlayerListFooter(@NotNull ComponentLike footer) {}
  
  public void sendPlayerListHeaderAndFooter(@NotNull ComponentLike header, @NotNull ComponentLike footer) {}
  
  public void openBook(Book.Builder book) {}
  
  public void sendResourcePacks(@NotNull ResourcePackInfoLike request, @NotNull ResourcePackInfoLike... others) {}
  
  public void removeResourcePacks(@NotNull ResourcePackRequest request) {}
  
  public void removeResourcePacks(@NotNull ResourcePackInfoLike request, @NotNull ResourcePackInfoLike... others) {}
  
  public boolean equals(Object that) {
    return (this == that);
  }
  
  public int hashCode() {
    return 0;
  }
  
  public String toString() {
    return "EmptyAudience";
  }
}
