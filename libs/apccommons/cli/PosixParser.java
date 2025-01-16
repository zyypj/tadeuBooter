package me.syncwrld.booter.libs.apccommons.cli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Deprecated
public class PosixParser extends Parser {
  private final List<String> tokens = new ArrayList<>();
  
  private boolean eatTheRest;
  
  private Option currentOption;
  
  private Options options;
  
  protected void burstToken(String token, boolean stopAtNonOption) {
    for (int i = 1; i < token.length(); i++) {
      String ch = String.valueOf(token.charAt(i));
      if (!this.options.hasOption(ch)) {
        if (stopAtNonOption) {
          processNonOptionToken(token.substring(i), true);
          break;
        } 
        this.tokens.add(token);
        break;
      } 
      this.tokens.add("-" + ch);
      this.currentOption = this.options.getOption(ch);
      if (this.currentOption.hasArg() && token.length() != i + 1) {
        this.tokens.add(token.substring(i + 1));
        break;
      } 
    } 
  }
  
  protected String[] flatten(Options options, String[] arguments, boolean stopAtNonOption) throws ParseException {
    init();
    this.options = options;
    Iterator<String> iter = Arrays.<String>asList(arguments).iterator();
    while (iter.hasNext()) {
      String token = iter.next();
      if ("-".equals(token) || "--".equals(token)) {
        this.tokens.add(token);
      } else if (token.startsWith("--")) {
        int pos = token.indexOf('=');
        String opt = (pos == -1) ? token : token.substring(0, pos);
        List<String> matchingOpts = options.getMatchingOptions(opt);
        if (matchingOpts.isEmpty()) {
          processNonOptionToken(token, stopAtNonOption);
        } else {
          if (matchingOpts.size() > 1)
            throw new AmbiguousOptionException(opt, matchingOpts); 
          this.currentOption = options.getOption(matchingOpts.get(0));
          this.tokens.add("--" + this.currentOption.getLongOpt());
          if (pos != -1)
            this.tokens.add(token.substring(pos + 1)); 
        } 
      } else if (token.startsWith("-")) {
        if (token.length() == 2 || options.hasOption(token)) {
          processOptionToken(token, stopAtNonOption);
        } else if (!options.getMatchingOptions(token).isEmpty()) {
          List<String> matchingOpts = options.getMatchingOptions(token);
          if (matchingOpts.size() > 1)
            throw new AmbiguousOptionException(token, matchingOpts); 
          Option opt = options.getOption(matchingOpts.get(0));
          processOptionToken("-" + opt.getLongOpt(), stopAtNonOption);
        } else {
          burstToken(token, stopAtNonOption);
        } 
      } else {
        processNonOptionToken(token, stopAtNonOption);
      } 
      gobble(iter);
    } 
    return this.tokens.<String>toArray(Util.EMPTY_STRING_ARRAY);
  }
  
  private void gobble(Iterator<String> iter) {
    if (this.eatTheRest)
      while (iter.hasNext())
        this.tokens.add(iter.next());  
  }
  
  private void init() {
    this.eatTheRest = false;
    this.tokens.clear();
  }
  
  private void processNonOptionToken(String value, boolean stopAtNonOption) {
    if (stopAtNonOption && (this.currentOption == null || !this.currentOption.hasArg())) {
      this.eatTheRest = true;
      this.tokens.add("--");
    } 
    this.tokens.add(value);
  }
  
  private void processOptionToken(String token, boolean stopAtNonOption) {
    if (stopAtNonOption && !this.options.hasOption(token))
      this.eatTheRest = true; 
    if (this.options.hasOption(token))
      this.currentOption = this.options.getOption(token); 
    this.tokens.add(token);
  }
}
