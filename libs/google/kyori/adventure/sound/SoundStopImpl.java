package me.syncwrld.booter.libs.google.kyori.adventure.sound;

import java.util.Objects;
import java.util.stream.Stream;
import me.syncwrld.booter.libs.google.kyori.adventure.internal.Internals;
import me.syncwrld.booter.libs.google.kyori.adventure.key.Key;
import me.syncwrld.booter.libs.google.kyori.examination.ExaminableProperty;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

abstract class SoundStopImpl implements SoundStop {
  static final SoundStop ALL = new SoundStopImpl(null) {
      @Nullable
      public Key sound() {
        return null;
      }
    };
  
  private final Sound.Source source;
  
  SoundStopImpl(Sound.Source source) {
    this.source = source;
  }
  
  public Sound.Source source() {
    return this.source;
  }
  
  public boolean equals(@Nullable Object other) {
    if (this == other)
      return true; 
    if (!(other instanceof SoundStopImpl))
      return false; 
    SoundStopImpl that = (SoundStopImpl)other;
    return (Objects.equals(sound(), that.sound()) && 
      Objects.equals(this.source, that.source));
  }
  
  public int hashCode() {
    int result = Objects.hashCode(sound());
    result = 31 * result + Objects.hashCode(this.source);
    return result;
  }
  
  @NotNull
  public Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(new ExaminableProperty[] { ExaminableProperty.of("name", sound()), 
          ExaminableProperty.of("source", this.source) });
  }
  
  public String toString() {
    return Internals.toString(this);
  }
}
