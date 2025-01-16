package me.syncwrld.booter.libs.google.kyori.adventure.chat;

import java.util.Objects;
import java.util.stream.Stream;
import me.syncwrld.booter.libs.google.kyori.adventure.key.Key;
import me.syncwrld.booter.libs.google.kyori.adventure.key.Keyed;
import me.syncwrld.booter.libs.google.kyori.adventure.text.Component;
import me.syncwrld.booter.libs.google.kyori.adventure.text.ComponentLike;
import me.syncwrld.booter.libs.google.kyori.examination.Examinable;
import me.syncwrld.booter.libs.google.kyori.examination.ExaminableProperty;
import me.syncwrld.booter.libs.jtann.Contract;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

public interface ChatType extends Examinable, Keyed {
  public static final ChatType CHAT = new ChatTypeImpl(Key.key("chat"));
  
  public static final ChatType SAY_COMMAND = new ChatTypeImpl(Key.key("say_command"));
  
  public static final ChatType MSG_COMMAND_INCOMING = new ChatTypeImpl(Key.key("msg_command_incoming"));
  
  public static final ChatType MSG_COMMAND_OUTGOING = new ChatTypeImpl(Key.key("msg_command_outgoing"));
  
  public static final ChatType TEAM_MSG_COMMAND_INCOMING = new ChatTypeImpl(Key.key("team_msg_command_incoming"));
  
  public static final ChatType TEAM_MSG_COMMAND_OUTGOING = new ChatTypeImpl(Key.key("team_msg_command_outgoing"));
  
  public static final ChatType EMOTE_COMMAND = new ChatTypeImpl(Key.key("emote_command"));
  
  @NotNull
  static ChatType chatType(@NotNull Keyed key) {
    return (key instanceof ChatType) ? (ChatType)key : new ChatTypeImpl(((Keyed)Objects.<Keyed>requireNonNull(key, "key")).key());
  }
  
  @Contract(value = "_ -> new", pure = true)
  default Bound bind(@NotNull ComponentLike name) {
    return bind(name, null);
  }
  
  @Contract(value = "_, _ -> new", pure = true)
  default Bound bind(@NotNull ComponentLike name, @Nullable ComponentLike target) {
    return new ChatTypeImpl.BoundImpl(this, Objects.<Component>requireNonNull(name.asComponent(), "name"), ComponentLike.unbox(target));
  }
  
  @NotNull
  default Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(ExaminableProperty.of("key", key()));
  }
  
  public static interface Bound extends Examinable {
    @Contract(pure = true)
    @NotNull
    ChatType type();
    
    @Contract(pure = true)
    @NotNull
    Component name();
    
    @Contract(pure = true)
    @Nullable
    Component target();
    
    @NotNull
    default Stream<? extends ExaminableProperty> examinableProperties() {
      return Stream.of(new ExaminableProperty[] { ExaminableProperty.of("type", type()), 
            ExaminableProperty.of("name", name()), 
            ExaminableProperty.of("target", target()) });
    }
  }
}
