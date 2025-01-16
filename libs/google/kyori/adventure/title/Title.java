package me.syncwrld.booter.libs.google.kyori.adventure.title;

import java.time.Duration;
import me.syncwrld.booter.libs.google.kyori.adventure.text.Component;
import me.syncwrld.booter.libs.google.kyori.adventure.util.Ticks;
import me.syncwrld.booter.libs.google.kyori.examination.Examinable;
import me.syncwrld.booter.libs.jtann.ApiStatus.NonExtendable;
import me.syncwrld.booter.libs.jtann.ApiStatus.ScheduledForRemoval;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

@NonExtendable
public interface Title extends Examinable {
  public static final Times DEFAULT_TIMES = Times.times(Ticks.duration(10L), Ticks.duration(70L), Ticks.duration(20L));
  
  @NotNull
  static Title title(@NotNull Component title, @NotNull Component subtitle) {
    return title(title, subtitle, DEFAULT_TIMES);
  }
  
  @NotNull
  static Title title(@NotNull Component title, @NotNull Component subtitle, @Nullable Times times) {
    return new TitleImpl(title, subtitle, times);
  }
  
  @NotNull
  Component title();
  
  @NotNull
  Component subtitle();
  
  @Nullable
  Times times();
  
  <T> T part(@NotNull TitlePart<T> paramTitlePart);
  
  public static interface Times extends Examinable {
    @Deprecated
    @ScheduledForRemoval(inVersion = "5.0.0")
    @NotNull
    static Times of(@NotNull Duration fadeIn, @NotNull Duration stay, @NotNull Duration fadeOut) {
      return times(fadeIn, stay, fadeOut);
    }
    
    @NotNull
    static Times times(@NotNull Duration fadeIn, @NotNull Duration stay, @NotNull Duration fadeOut) {
      return new TitleImpl.TimesImpl(fadeIn, stay, fadeOut);
    }
    
    @NotNull
    Duration fadeIn();
    
    @NotNull
    Duration stay();
    
    @NotNull
    Duration fadeOut();
  }
}
