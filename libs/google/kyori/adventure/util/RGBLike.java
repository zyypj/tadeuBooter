package me.syncwrld.booter.libs.google.kyori.adventure.util;

import me.syncwrld.booter.libs.jtann.NotNull;

public interface RGBLike {
  int red();
  
  int green();
  
  int blue();
  
  @NotNull
  default HSVLike asHSV() {
    return HSVLike.fromRGB(red(), green(), blue());
  }
}
