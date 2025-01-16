package me.syncwrld.booter.libs.apccommons.cli;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class GnuParser extends Parser {
  protected String[] flatten(Options options, String[] arguments, boolean stopAtNonOption) {
    List<String> tokens = new ArrayList<>();
    boolean eatTheRest = false;
    for (int i = 0; i < arguments.length; i++) {
      String arg = arguments[i];
      if ("--".equals(arg)) {
        eatTheRest = true;
        tokens.add("--");
      } else if ("-".equals(arg)) {
        tokens.add("-");
      } else if (arg.startsWith("-")) {
        String opt = Util.stripLeadingHyphens(arg);
        if (options.hasOption(opt)) {
          tokens.add(arg);
        } else if (opt.indexOf('=') != -1 && options.hasOption(opt.substring(0, opt.indexOf('=')))) {
          tokens.add(arg.substring(0, arg.indexOf('=')));
          tokens.add(arg.substring(arg.indexOf('=') + 1));
        } else if (options.hasOption(arg.substring(0, 2))) {
          tokens.add(arg.substring(0, 2));
          tokens.add(arg.substring(2));
        } else {
          eatTheRest = stopAtNonOption;
          tokens.add(arg);
        } 
      } else {
        tokens.add(arg);
      } 
      if (eatTheRest)
        for (; ++i < arguments.length; i++)
          tokens.add(arguments[i]);  
    } 
    return tokens.<String>toArray(Util.EMPTY_STRING_ARRAY);
  }
}
