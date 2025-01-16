package me.syncwrld.booter.libs.google.kyori.adventure.text.serializer.gson;

import me.syncwrld.booter.libs.google.gson.TypeAdapter;
import me.syncwrld.booter.libs.google.kyori.adventure.text.format.TextDecoration;

final class TextDecorationSerializer {
  static final TypeAdapter<TextDecoration> INSTANCE = IndexedSerializer.strict("text decoration", TextDecoration.NAMES);
}
