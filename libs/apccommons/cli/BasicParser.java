package me.syncwrld.booter.libs.apccommons.cli;

@Deprecated
public class BasicParser extends Parser {
  protected String[] flatten(Options options, String[] arguments, boolean stopAtNonOption) {
    return arguments;
  }
}
