package me.syncwrld.booter.libs.apccommons.cli;

final class Util {
  static final String[] EMPTY_STRING_ARRAY = new String[0];
  
  static String stripLeadingAndTrailingQuotes(String str) {
    int length = str.length();
    if (length > 1 && str.startsWith("\"") && str.endsWith("\"") && str.substring(1, length - 1).indexOf('"') == -1)
      str = str.substring(1, length - 1); 
    return str;
  }
  
  static String stripLeadingHyphens(String str) {
    if (str == null)
      return null; 
    if (str.startsWith("--"))
      return str.substring(2); 
    if (str.startsWith("-"))
      return str.substring(1); 
    return str;
  }
}
