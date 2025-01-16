package me.syncwrld.booter.libs.google.kyori.adventure.title;

import me.syncwrld.booter.libs.google.kyori.adventure.text.Component;
import me.syncwrld.booter.libs.jtann.ApiStatus.NonExtendable;

@NonExtendable
public interface TitlePart<T> {
  public static final TitlePart<Component> TITLE = new TitlePart<Component>() {
      public String toString() {
        return "TitlePart.TITLE";
      }
    };
  
  public static final TitlePart<Component> SUBTITLE = new TitlePart<Component>() {
      public String toString() {
        return "TitlePart.SUBTITLE";
      }
    };
  
  public static final TitlePart<Title.Times> TIMES = new TitlePart<Title.Times>() {
      public String toString() {
        return "TitlePart.TIMES";
      }
    };
}
