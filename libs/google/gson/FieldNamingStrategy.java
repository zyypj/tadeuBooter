package me.syncwrld.booter.libs.google.gson;

import java.lang.reflect.Field;

public interface FieldNamingStrategy {
  String translateName(Field paramField);
}
