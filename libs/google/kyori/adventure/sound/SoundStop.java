package me.syncwrld.booter.libs.google.kyori.adventure.sound;

import java.util.Objects;
import java.util.function.Supplier;
import me.syncwrld.booter.libs.google.kyori.adventure.key.Key;
import me.syncwrld.booter.libs.google.kyori.examination.Examinable;
import me.syncwrld.booter.libs.jtann.ApiStatus.NonExtendable;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

@NonExtendable
public interface SoundStop extends Examinable {
  @NotNull
  static SoundStop all() {
    return SoundStopImpl.ALL;
  }
  
  @NotNull
  static SoundStop named(@NotNull final Key sound) {
    Objects.requireNonNull(sound, "sound");
    return new SoundStopImpl(null) {
        @NotNull
        public Key sound() {
          return sound;
        }
      };
  }
  
  @NotNull
  static SoundStop named(final Sound.Type sound) {
    Objects.requireNonNull(sound, "sound");
    return new SoundStopImpl(null) {
        @NotNull
        public Key sound() {
          return sound.key();
        }
      };
  }
  
  @NotNull
  static SoundStop named(@NotNull final Supplier<? extends Sound.Type> sound) {
    Objects.requireNonNull(sound, "sound");
    return new SoundStopImpl(null) {
        @NotNull
        public Key sound() {
          return ((Sound.Type)sound.get()).key();
        }
      };
  }
  
  @NotNull
  static SoundStop source(Sound.Source source) {
    Objects.requireNonNull(source, "source");
    return new SoundStopImpl(source) {
        @Nullable
        public Key sound() {
          return null;
        }
      };
  }
  
  @NotNull
  static SoundStop namedOnSource(@NotNull final Key sound, Sound.Source source) {
    Objects.requireNonNull(sound, "sound");
    Objects.requireNonNull(source, "source");
    return new SoundStopImpl(source) {
        @NotNull
        public Key sound() {
          return sound;
        }
      };
  }
  
  @NotNull
  static SoundStop namedOnSource(Sound.Type sound, Sound.Source source) {
    Objects.requireNonNull(sound, "sound");
    return namedOnSource(sound.key(), source);
  }
  
  @NotNull
  static SoundStop namedOnSource(@NotNull final Supplier<? extends Sound.Type> sound, Sound.Source source) {
    Objects.requireNonNull(sound, "sound");
    Objects.requireNonNull(source, "source");
    return new SoundStopImpl(source) {
        @NotNull
        public Key sound() {
          return ((Sound.Type)sound.get()).key();
        }
      };
  }
  
  @Nullable
  Key sound();
  
  Sound.Source source();
}
