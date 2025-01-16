package me.syncwrld.booter.libs.apccommons.cli;

final class OptionValidator {
  private static boolean isValidChar(char c) {
    return Character.isJavaIdentifierPart(c);
  }
  
  private static boolean isValidOpt(char c) {
    return (isValidChar(c) || c == '?' || c == '@');
  }
  
  static String validate(String option) throws IllegalArgumentException {
    if (option == null)
      return null; 
    if (option.length() == 1) {
      char ch = option.charAt(0);
      if (!isValidOpt(ch))
        throw new IllegalArgumentException("Illegal option name '" + ch + "'"); 
    } else {
      for (char ch : option.toCharArray()) {
        if (!isValidChar(ch))
          throw new IllegalArgumentException("The option '" + option + "' contains an illegal character : '" + ch + "'"); 
      } 
    } 
    return option;
  }
}
