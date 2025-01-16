package me.syncwrld.booter.libs.google.kyori.examination;

import java.util.stream.Stream;
import me.syncwrld.booter.libs.jtann.NotNull;

public interface Examinable {
  @NotNull
  default String examinableName() {
    return getClass().getSimpleName();
  }
  
  @NotNull
  default Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.empty();
  }
  
  @NotNull
  default <R> R examine(@NotNull Examiner<R> examiner) {
    return examiner.examine(this);
  }
}
