package me.syncwrld.booter.libs.google.kyori.adventure.text.serializer.gson;

import me.syncwrld.booter.libs.google.gson.TypeAdapter;
import me.syncwrld.booter.libs.google.kyori.adventure.text.event.HoverEvent;

final class HoverEventActionSerializer {
  static final TypeAdapter<HoverEvent.Action<?>> INSTANCE = IndexedSerializer.lenient("hover action", HoverEvent.Action.NAMES);
}
