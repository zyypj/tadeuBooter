package me.syncwrld.booter.libs.google.kyori.adventure.internal;

import me.syncwrld.booter.libs.google.kyori.examination.Examinable;
import me.syncwrld.booter.libs.google.kyori.examination.Examiner;
import me.syncwrld.booter.libs.google.kyori.examination.string.StringExaminer;
import me.syncwrld.booter.libs.jtann.ApiStatus.Internal;
import me.syncwrld.booter.libs.jtann.NotNull;

@Internal
public final class Internals {
  @NotNull
  public static String toString(@NotNull Examinable examinable) {
    return (String)examinable.examine((Examiner)StringExaminer.simpleEscaping());
  }
}
