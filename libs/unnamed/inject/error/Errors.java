package me.syncwrld.booter.libs.unnamed.inject.error;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import me.syncwrld.booter.libs.unnamed.inject.util.Validate;

final class Errors {
  static String getStackTrace(Throwable throwable) {
    Validate.notNull(throwable);
    StringWriter writer = new StringWriter();
    throwable.printStackTrace(new PrintWriter(writer));
    return writer.toString();
  }
  
  static String formatErrorMessages(List<String> messages) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < messages.size(); i++) {
      builder.append("\n");
      builder.append(i + 1);
      builder.append(") ");
      builder.append(messages.get(i));
    } 
    return builder.toString();
  }
}
