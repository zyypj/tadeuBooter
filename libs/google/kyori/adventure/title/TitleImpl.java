package me.syncwrld.booter.libs.google.kyori.adventure.title;

import java.time.Duration;
import java.util.Objects;
import java.util.stream.Stream;
import me.syncwrld.booter.libs.google.kyori.adventure.internal.Internals;
import me.syncwrld.booter.libs.google.kyori.adventure.text.Component;
import me.syncwrld.booter.libs.google.kyori.examination.ExaminableProperty;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

final class TitleImpl implements Title {
  private final Component title;
  
  private final Component subtitle;
  
  @Nullable
  private final Title.Times times;
  
  TitleImpl(@NotNull Component title, @NotNull Component subtitle, @Nullable Title.Times times) {
    this.title = Objects.<Component>requireNonNull(title, "title");
    this.subtitle = Objects.<Component>requireNonNull(subtitle, "subtitle");
    this.times = times;
  }
  
  @NotNull
  public Component title() {
    return this.title;
  }
  
  @NotNull
  public Component subtitle() {
    return this.subtitle;
  }
  
  @Nullable
  public Title.Times times() {
    return this.times;
  }
  
  public <T> T part(@NotNull TitlePart<T> part) {
    Objects.requireNonNull(part, "part");
    if (part == TitlePart.TITLE)
      return (T)this.title; 
    if (part == TitlePart.SUBTITLE)
      return (T)this.subtitle; 
    if (part == TitlePart.TIMES)
      return (T)this.times; 
    throw new IllegalArgumentException("Don't know what " + part + " is.");
  }
  
  public boolean equals(@Nullable Object other) {
    if (this == other)
      return true; 
    if (other == null || getClass() != other.getClass())
      return false; 
    TitleImpl that = (TitleImpl)other;
    return (this.title.equals(that.title) && this.subtitle
      .equals(that.subtitle) && 
      Objects.equals(this.times, that.times));
  }
  
  public int hashCode() {
    int result = this.title.hashCode();
    result = 31 * result + this.subtitle.hashCode();
    result = 31 * result + Objects.hashCode(this.times);
    return result;
  }
  
  @NotNull
  public Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(new ExaminableProperty[] { ExaminableProperty.of("title", this.title), 
          ExaminableProperty.of("subtitle", this.subtitle), 
          ExaminableProperty.of("times", this.times) });
  }
  
  public String toString() {
    return Internals.toString(this);
  }
  
  static class TimesImpl implements Title.Times {
    private final Duration fadeIn;
    
    private final Duration stay;
    
    private final Duration fadeOut;
    
    TimesImpl(@NotNull Duration fadeIn, @NotNull Duration stay, @NotNull Duration fadeOut) {
      this.fadeIn = Objects.<Duration>requireNonNull(fadeIn, "fadeIn");
      this.stay = Objects.<Duration>requireNonNull(stay, "stay");
      this.fadeOut = Objects.<Duration>requireNonNull(fadeOut, "fadeOut");
    }
    
    @NotNull
    public Duration fadeIn() {
      return this.fadeIn;
    }
    
    @NotNull
    public Duration stay() {
      return this.stay;
    }
    
    @NotNull
    public Duration fadeOut() {
      return this.fadeOut;
    }
    
    public boolean equals(@Nullable Object other) {
      if (this == other)
        return true; 
      if (!(other instanceof TimesImpl))
        return false; 
      TimesImpl that = (TimesImpl)other;
      return (this.fadeIn.equals(that.fadeIn) && this.stay
        .equals(that.stay) && this.fadeOut
        .equals(that.fadeOut));
    }
    
    public int hashCode() {
      int result = this.fadeIn.hashCode();
      result = 31 * result + this.stay.hashCode();
      result = 31 * result + this.fadeOut.hashCode();
      return result;
    }
    
    @NotNull
    public Stream<? extends ExaminableProperty> examinableProperties() {
      return Stream.of(new ExaminableProperty[] { ExaminableProperty.of("fadeIn", this.fadeIn), 
            ExaminableProperty.of("stay", this.stay), 
            ExaminableProperty.of("fadeOut", this.fadeOut) });
    }
    
    public String toString() {
      return Internals.toString(this);
    }
  }
}
