package me.syncwrld.booter.libs.google.kyori.adventure.text.format;

import java.util.stream.Stream;
import me.syncwrld.booter.libs.google.kyori.examination.Examinable;
import me.syncwrld.booter.libs.google.kyori.examination.ExaminableProperty;
import me.syncwrld.booter.libs.jtann.ApiStatus.NonExtendable;
import me.syncwrld.booter.libs.jtann.NotNull;

@NonExtendable
public interface TextDecorationAndState extends Examinable, StyleBuilderApplicable {
  @NotNull
  TextDecoration decoration();
  
  TextDecoration.State state();
  
  default void styleApply(Style.Builder style) {
    style.decoration(decoration(), state());
  }
  
  @NotNull
  default Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(new ExaminableProperty[] { ExaminableProperty.of("decoration", decoration()), 
          ExaminableProperty.of("state", state()) });
  }
}
