package me.syncwrld.booter.libs.google.kyori.adventure.chat;

import me.syncwrld.booter.libs.google.kyori.adventure.internal.Internals;
import me.syncwrld.booter.libs.google.kyori.adventure.key.Key;
import me.syncwrld.booter.libs.google.kyori.adventure.text.Component;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

final class ChatTypeImpl implements ChatType {
  private final Key key;
  
  ChatTypeImpl(@NotNull Key key) {
    this.key = key;
  }
  
  @NotNull
  public Key key() {
    return this.key;
  }
  
  public String toString() {
    return Internals.toString(this);
  }
  
  static final class BoundImpl implements ChatType.Bound {
    private final ChatType chatType;
    
    private final Component name;
    
    @Nullable
    private final Component target;
    
    BoundImpl(ChatType chatType, Component name, @Nullable Component target) {
      this.chatType = chatType;
      this.name = name;
      this.target = target;
    }
    
    @NotNull
    public ChatType type() {
      return this.chatType;
    }
    
    @NotNull
    public Component name() {
      return this.name;
    }
    
    @Nullable
    public Component target() {
      return this.target;
    }
    
    public String toString() {
      return Internals.toString(this);
    }
  }
}
