package me.syncwrld.booter.libs.google.kyori.adventure.text;

import me.syncwrld.booter.libs.jtann.Contract;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

public interface NBTComponentBuilder<C extends NBTComponent<C, B>, B extends NBTComponentBuilder<C, B>> extends ComponentBuilder<C, B> {
  @Contract("_ -> this")
  @NotNull
  B nbtPath(@NotNull String paramString);
  
  @Contract("_ -> this")
  @NotNull
  B interpret(boolean paramBoolean);
  
  @Contract("_ -> this")
  @NotNull
  B separator(@Nullable ComponentLike paramComponentLike);
}
