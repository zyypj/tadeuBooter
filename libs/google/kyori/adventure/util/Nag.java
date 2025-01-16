package me.syncwrld.booter.libs.google.kyori.adventure.util;

import me.syncwrld.booter.libs.jtann.NotNull;

public abstract class Nag extends RuntimeException {
  private static final long serialVersionUID = -695562541413409498L;
  
  public static void print(@NotNull Nag nag) {
    nag.printStackTrace();
  }
  
  protected Nag(String message) {
    super(message);
  }
}
