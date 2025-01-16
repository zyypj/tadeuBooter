package me.syncwrld.booter.libs.google.kyori.adventure.text;

import java.util.stream.Stream;
import me.syncwrld.booter.libs.google.kyori.examination.ExaminableProperty;
import me.syncwrld.booter.libs.jtann.Contract;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

public interface NBTComponent<C extends NBTComponent<C, B>, B extends NBTComponentBuilder<C, B>> extends BuildableComponent<C, B> {
  @NotNull
  String nbtPath();
  
  @Contract(pure = true)
  @NotNull
  C nbtPath(@NotNull String paramString);
  
  boolean interpret();
  
  @Contract(pure = true)
  @NotNull
  C interpret(boolean paramBoolean);
  
  @Nullable
  Component separator();
  
  @NotNull
  C separator(@Nullable ComponentLike paramComponentLike);
  
  @NotNull
  default Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.concat(
        Stream.of(new ExaminableProperty[] { ExaminableProperty.of("nbtPath", nbtPath()), 
            ExaminableProperty.of("interpret", interpret()), 
            ExaminableProperty.of("separator", separator()) }), super.examinableProperties());
  }
}
