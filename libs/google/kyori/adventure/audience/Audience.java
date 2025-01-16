package me.syncwrld.booter.libs.google.kyori.adventure.audience;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collector;
import me.syncwrld.booter.libs.google.kyori.adventure.bossbar.BossBar;
import me.syncwrld.booter.libs.google.kyori.adventure.chat.ChatType;
import me.syncwrld.booter.libs.google.kyori.adventure.chat.SignedMessage;
import me.syncwrld.booter.libs.google.kyori.adventure.identity.Identified;
import me.syncwrld.booter.libs.google.kyori.adventure.identity.Identity;
import me.syncwrld.booter.libs.google.kyori.adventure.inventory.Book;
import me.syncwrld.booter.libs.google.kyori.adventure.pointer.Pointered;
import me.syncwrld.booter.libs.google.kyori.adventure.resource.ResourcePackInfo;
import me.syncwrld.booter.libs.google.kyori.adventure.resource.ResourcePackInfoLike;
import me.syncwrld.booter.libs.google.kyori.adventure.resource.ResourcePackRequest;
import me.syncwrld.booter.libs.google.kyori.adventure.resource.ResourcePackRequestLike;
import me.syncwrld.booter.libs.google.kyori.adventure.sound.Sound;
import me.syncwrld.booter.libs.google.kyori.adventure.sound.SoundStop;
import me.syncwrld.booter.libs.google.kyori.adventure.text.Component;
import me.syncwrld.booter.libs.google.kyori.adventure.text.ComponentLike;
import me.syncwrld.booter.libs.google.kyori.adventure.title.Title;
import me.syncwrld.booter.libs.google.kyori.adventure.title.TitlePart;
import me.syncwrld.booter.libs.jtann.ApiStatus.ScheduledForRemoval;
import me.syncwrld.booter.libs.jtann.NotNull;

public interface Audience extends Pointered {
  @NotNull
  static Audience empty() {
    return EmptyAudience.INSTANCE;
  }
  
  @NotNull
  static Audience audience(@NotNull Audience... audiences) {
    int length = audiences.length;
    if (length == 0)
      return empty(); 
    if (length == 1)
      return audiences[0]; 
    return audience(Arrays.asList(audiences));
  }
  
  @NotNull
  static ForwardingAudience audience(@NotNull Iterable<? extends Audience> audiences) {
    return () -> audiences;
  }
  
  @NotNull
  static Collector<? super Audience, ?, ForwardingAudience> toAudience() {
    return Audiences.COLLECTOR;
  }
  
  @NotNull
  default Audience filterAudience(@NotNull Predicate<? super Audience> filter) {
    return filter.test(this) ? 
      this : 
      empty();
  }
  
  default void forEachAudience(@NotNull Consumer<? super Audience> action) {
    action.accept(this);
  }
  
  @ForwardingAudienceOverrideNotRequired
  default void sendMessage(@NotNull ComponentLike message) {
    sendMessage(message.asComponent());
  }
  
  default void sendMessage(@NotNull Component message) {
    sendMessage(message, MessageType.SYSTEM);
  }
  
  @Deprecated
  @ForwardingAudienceOverrideNotRequired
  @ScheduledForRemoval(inVersion = "5.0.0")
  default void sendMessage(@NotNull ComponentLike message, @NotNull MessageType type) {
    sendMessage(message.asComponent(), type);
  }
  
  @Deprecated
  @ForwardingAudienceOverrideNotRequired
  @ScheduledForRemoval(inVersion = "5.0.0")
  default void sendMessage(@NotNull Component message, @NotNull MessageType type) {
    sendMessage(Identity.nil(), message, type);
  }
  
  @Deprecated
  @ForwardingAudienceOverrideNotRequired
  default void sendMessage(@NotNull Identified source, @NotNull ComponentLike message) {
    sendMessage(source, message.asComponent());
  }
  
  @Deprecated
  @ForwardingAudienceOverrideNotRequired
  default void sendMessage(@NotNull Identity source, @NotNull ComponentLike message) {
    sendMessage(source, message.asComponent());
  }
  
  @Deprecated
  @ForwardingAudienceOverrideNotRequired
  default void sendMessage(@NotNull Identified source, @NotNull Component message) {
    sendMessage(source, message, MessageType.CHAT);
  }
  
  @Deprecated
  @ForwardingAudienceOverrideNotRequired
  default void sendMessage(@NotNull Identity source, @NotNull Component message) {
    sendMessage(source, message, MessageType.CHAT);
  }
  
  @Deprecated
  @ForwardingAudienceOverrideNotRequired
  @ScheduledForRemoval(inVersion = "5.0.0")
  default void sendMessage(@NotNull Identified source, @NotNull ComponentLike message, @NotNull MessageType type) {
    sendMessage(source, message.asComponent(), type);
  }
  
  @Deprecated
  @ForwardingAudienceOverrideNotRequired
  @ScheduledForRemoval(inVersion = "5.0.0")
  default void sendMessage(@NotNull Identity source, @NotNull ComponentLike message, @NotNull MessageType type) {
    sendMessage(source, message.asComponent(), type);
  }
  
  @Deprecated
  @ScheduledForRemoval(inVersion = "5.0.0")
  default void sendMessage(@NotNull Identified source, @NotNull Component message, @NotNull MessageType type) {
    sendMessage(source.identity(), message, type);
  }
  
  @Deprecated
  @ScheduledForRemoval(inVersion = "5.0.0")
  default void sendMessage(@NotNull Identity source, @NotNull Component message, @NotNull MessageType type) {}
  
  default void sendMessage(@NotNull Component message, ChatType.Bound boundChatType) {
    sendMessage(message, MessageType.CHAT);
  }
  
  @ForwardingAudienceOverrideNotRequired
  default void sendMessage(@NotNull ComponentLike message, ChatType.Bound boundChatType) {
    sendMessage(message.asComponent(), boundChatType);
  }
  
  default void sendMessage(@NotNull SignedMessage signedMessage, ChatType.Bound boundChatType) {
    Component content = (signedMessage.unsignedContent() != null) ? signedMessage.unsignedContent() : (Component)Component.text(signedMessage.message());
    if (signedMessage.isSystem()) {
      sendMessage(content);
    } else {
      sendMessage(signedMessage.identity(), content, MessageType.CHAT);
    } 
  }
  
  @ForwardingAudienceOverrideNotRequired
  default void deleteMessage(@NotNull SignedMessage signedMessage) {
    if (signedMessage.canDelete())
      deleteMessage(Objects.<SignedMessage.Signature>requireNonNull(signedMessage.signature())); 
  }
  
  default void deleteMessage(SignedMessage.Signature signature) {}
  
  @ForwardingAudienceOverrideNotRequired
  default void sendActionBar(@NotNull ComponentLike message) {
    sendActionBar(message.asComponent());
  }
  
  default void sendActionBar(@NotNull Component message) {}
  
  @ForwardingAudienceOverrideNotRequired
  default void sendPlayerListHeader(@NotNull ComponentLike header) {
    sendPlayerListHeader(header.asComponent());
  }
  
  default void sendPlayerListHeader(@NotNull Component header) {
    sendPlayerListHeaderAndFooter(header, (Component)Component.empty());
  }
  
  @ForwardingAudienceOverrideNotRequired
  default void sendPlayerListFooter(@NotNull ComponentLike footer) {
    sendPlayerListFooter(footer.asComponent());
  }
  
  default void sendPlayerListFooter(@NotNull Component footer) {
    sendPlayerListHeaderAndFooter((Component)Component.empty(), footer);
  }
  
  @ForwardingAudienceOverrideNotRequired
  default void sendPlayerListHeaderAndFooter(@NotNull ComponentLike header, @NotNull ComponentLike footer) {
    sendPlayerListHeaderAndFooter(header.asComponent(), footer.asComponent());
  }
  
  default void sendPlayerListHeaderAndFooter(@NotNull Component header, @NotNull Component footer) {}
  
  @ForwardingAudienceOverrideNotRequired
  default void showTitle(@NotNull Title title) {
    Title.Times times = title.times();
    if (times != null)
      sendTitlePart(TitlePart.TIMES, times); 
    sendTitlePart(TitlePart.SUBTITLE, title.subtitle());
    sendTitlePart(TitlePart.TITLE, title.title());
  }
  
  default <T> void sendTitlePart(@NotNull TitlePart<T> part, @NotNull T value) {}
  
  default void clearTitle() {}
  
  default void resetTitle() {}
  
  default void showBossBar(@NotNull BossBar bar) {}
  
  default void hideBossBar(@NotNull BossBar bar) {}
  
  default void playSound(@NotNull Sound sound) {}
  
  default void playSound(@NotNull Sound sound, double x, double y, double z) {}
  
  default void playSound(@NotNull Sound sound, Sound.Emitter emitter) {}
  
  @ForwardingAudienceOverrideNotRequired
  default void stopSound(@NotNull Sound sound) {
    stopSound(((Sound)Objects.<Sound>requireNonNull(sound, "sound")).asStop());
  }
  
  default void stopSound(@NotNull SoundStop stop) {}
  
  @ForwardingAudienceOverrideNotRequired
  default void openBook(Book.Builder book) {
    openBook(book.build());
  }
  
  default void openBook(@NotNull Book book) {}
  
  @ForwardingAudienceOverrideNotRequired
  void sendResourcePacks(@NotNull ResourcePackInfoLike first, @NotNull ResourcePackInfoLike... others) {
    sendResourcePacks(ResourcePackRequest.addingRequest(first, others));
  }
  
  @ForwardingAudienceOverrideNotRequired
  default void sendResourcePacks(@NotNull ResourcePackRequestLike request) {
    sendResourcePacks(request.asResourcePackRequest());
  }
  
  default void sendResourcePacks(@NotNull ResourcePackRequest request) {}
  
  @ForwardingAudienceOverrideNotRequired
  default void removeResourcePacks(@NotNull ResourcePackRequestLike request) {
    removeResourcePacks(request.asResourcePackRequest());
  }
  
  @ForwardingAudienceOverrideNotRequired
  default void removeResourcePacks(@NotNull ResourcePackRequest request) {
    List<ResourcePackInfo> infos = request.packs();
    if (infos.size() == 1) {
      removeResourcePacks(((ResourcePackInfo)infos.get(0)).id(), new UUID[0]);
    } else if (infos.isEmpty()) {
      return;
    } 
    UUID[] otherReqs = new UUID[infos.size() - 1];
    for (int i = 0; i < otherReqs.length; i++)
      otherReqs[i] = ((ResourcePackInfo)infos.get(i + 1)).id(); 
    removeResourcePacks(((ResourcePackInfo)infos.get(0)).id(), otherReqs);
  }
  
  @ForwardingAudienceOverrideNotRequired
  void removeResourcePacks(@NotNull ResourcePackInfoLike request, @NotNull ResourcePackInfoLike... others) {
    UUID[] otherReqs = new UUID[others.length];
    for (int i = 0; i < others.length; i++)
      otherReqs[i] = others[i].asResourcePackInfo().id(); 
    removeResourcePacks(request.asResourcePackInfo().id(), otherReqs);
  }
  
  default void removeResourcePacks(@NotNull Iterable<UUID> ids) {
    UUID[] others;
    Iterator<UUID> it = ids.iterator();
    if (!it.hasNext())
      return; 
    UUID id = it.next();
    if (!it.hasNext()) {
      others = new UUID[0];
    } else if (ids instanceof Collection) {
      others = new UUID[((Collection)ids).size() - 1];
      for (int i = 0; i < others.length; i++)
        others[i] = it.next(); 
    } else {
      List<UUID> othersList = new ArrayList<>();
      while (it.hasNext())
        othersList.add(it.next()); 
      others = othersList.<UUID>toArray(new UUID[0]);
    } 
    removeResourcePacks(id, others);
  }
  
  void removeResourcePacks(@NotNull UUID id, @NotNull UUID... others) {}
  
  default void clearResourcePacks() {}
}
