package me.syncwrld.booter.libs.apccommons.cli;

public interface CommandLineParser {
  CommandLine parse(Options paramOptions, String[] paramArrayOfString) throws ParseException;
  
  CommandLine parse(Options paramOptions, String[] paramArrayOfString, boolean paramBoolean) throws ParseException;
}
