package me.syncwrld.booter.libs.apccommons.cli;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

public class DefaultParser implements CommandLineParser {
  protected CommandLine cmd;
  
  protected Options options;
  
  protected boolean stopAtNonOption;
  
  protected String currentToken;
  
  protected Option currentOption;
  
  protected boolean skipParsing;
  
  protected List expectedOpts;
  
  private final boolean allowPartialMatching;
  
  private final Boolean stripLeadingAndTrailingQuotes;
  
  public static final class Builder {
    private boolean allowPartialMatching = true;
    
    private Boolean stripLeadingAndTrailingQuotes;
    
    public DefaultParser build() {
      return new DefaultParser(this.allowPartialMatching, this.stripLeadingAndTrailingQuotes);
    }
    
    public Builder setAllowPartialMatching(boolean allowPartialMatching) {
      this.allowPartialMatching = allowPartialMatching;
      return this;
    }
    
    public Builder setStripLeadingAndTrailingQuotes(Boolean stripLeadingAndTrailingQuotes) {
      this.stripLeadingAndTrailingQuotes = stripLeadingAndTrailingQuotes;
      return this;
    }
    
    private Builder() {}
  }
  
  public static Builder builder() {
    return new Builder();
  }
  
  public DefaultParser() {
    this.allowPartialMatching = true;
    this.stripLeadingAndTrailingQuotes = null;
  }
  
  public DefaultParser(boolean allowPartialMatching) {
    this.allowPartialMatching = allowPartialMatching;
    this.stripLeadingAndTrailingQuotes = null;
  }
  
  private DefaultParser(boolean allowPartialMatching, Boolean stripLeadingAndTrailingQuotes) {
    this.allowPartialMatching = allowPartialMatching;
    this.stripLeadingAndTrailingQuotes = stripLeadingAndTrailingQuotes;
  }
  
  private void checkRequiredArgs() throws ParseException {
    if (this.currentOption != null && this.currentOption.requiresArg())
      throw new MissingArgumentException(this.currentOption); 
  }
  
  protected void checkRequiredOptions() throws MissingOptionException {
    if (!this.expectedOpts.isEmpty())
      throw new MissingOptionException(this.expectedOpts); 
  }
  
  private String getLongPrefix(String token) {
    String t = Util.stripLeadingHyphens(token);
    String opt = null;
    for (int i = t.length() - 2; i > 1; i--) {
      String prefix = t.substring(0, i);
      if (this.options.hasLongOption(prefix)) {
        opt = prefix;
        break;
      } 
    } 
    return opt;
  }
  
  private List<String> getMatchingLongOptions(String token) {
    if (this.allowPartialMatching)
      return this.options.getMatchingOptions(token); 
    List<String> matches = new ArrayList<>(1);
    if (this.options.hasLongOption(token)) {
      Option option = this.options.getOption(token);
      matches.add(option.getLongOpt());
    } 
    return matches;
  }
  
  protected void handleConcatenatedOptions(String token) throws ParseException {
    for (int i = 1; i < token.length(); i++) {
      String ch = String.valueOf(token.charAt(i));
      if (!this.options.hasOption(ch)) {
        handleUnknownToken((this.stopAtNonOption && i > 1) ? token.substring(i) : token);
        break;
      } 
      handleOption(this.options.getOption(ch));
      if (this.currentOption != null && token.length() != i + 1) {
        this.currentOption.addValueForProcessing(stripLeadingAndTrailingQuotesDefaultOff(token.substring(i + 1)));
        break;
      } 
    } 
  }
  
  private void handleLongOption(String token) throws ParseException {
    if (token.indexOf('=') == -1) {
      handleLongOptionWithoutEqual(token);
    } else {
      handleLongOptionWithEqual(token);
    } 
  }
  
  private void handleLongOptionWithEqual(String token) throws ParseException {
    int pos = token.indexOf('=');
    String value = token.substring(pos + 1);
    String opt = token.substring(0, pos);
    List<String> matchingOpts = getMatchingLongOptions(opt);
    if (matchingOpts.isEmpty()) {
      handleUnknownToken(this.currentToken);
    } else {
      if (matchingOpts.size() > 1 && !this.options.hasLongOption(opt))
        throw new AmbiguousOptionException(opt, matchingOpts); 
      String key = this.options.hasLongOption(opt) ? opt : matchingOpts.get(0);
      Option option = this.options.getOption(key);
      if (option.acceptsArg()) {
        handleOption(option);
        this.currentOption.addValueForProcessing(stripLeadingAndTrailingQuotesDefaultOff(value));
        this.currentOption = null;
      } else {
        handleUnknownToken(this.currentToken);
      } 
    } 
  }
  
  private void handleLongOptionWithoutEqual(String token) throws ParseException {
    List<String> matchingOpts = getMatchingLongOptions(token);
    if (matchingOpts.isEmpty()) {
      handleUnknownToken(this.currentToken);
    } else {
      if (matchingOpts.size() > 1 && !this.options.hasLongOption(token))
        throw new AmbiguousOptionException(token, matchingOpts); 
      String key = this.options.hasLongOption(token) ? token : matchingOpts.get(0);
      handleOption(this.options.getOption(key));
    } 
  }
  
  private void handleOption(Option option) throws ParseException {
    checkRequiredArgs();
    option = (Option)option.clone();
    updateRequiredOptions(option);
    this.cmd.addOption(option);
    if (option.hasArg()) {
      this.currentOption = option;
    } else {
      this.currentOption = null;
    } 
  }
  
  private void handleProperties(Properties properties) throws ParseException {
    if (properties == null)
      return; 
    for (Enumeration<?> e = properties.propertyNames(); e.hasMoreElements(); ) {
      String option = e.nextElement().toString();
      Option opt = this.options.getOption(option);
      if (opt == null)
        throw new UnrecognizedOptionException("Default option wasn't defined", option); 
      OptionGroup group = this.options.getOptionGroup(opt);
      boolean selected = (group != null && group.getSelected() != null);
      if (!this.cmd.hasOption(option) && !selected) {
        String value = properties.getProperty(option);
        if (opt.hasArg()) {
          if (opt.getValues() == null || (opt.getValues()).length == 0)
            opt.addValueForProcessing(stripLeadingAndTrailingQuotesDefaultOff(value)); 
        } else if (!"yes".equalsIgnoreCase(value) && !"true".equalsIgnoreCase(value) && !"1".equalsIgnoreCase(value)) {
          continue;
        } 
        handleOption(opt);
        this.currentOption = null;
      } 
    } 
  }
  
  private void handleShortAndLongOption(String token) throws ParseException {
    String t = Util.stripLeadingHyphens(token);
    int pos = t.indexOf('=');
    if (t.length() == 1) {
      if (this.options.hasShortOption(t)) {
        handleOption(this.options.getOption(t));
      } else {
        handleUnknownToken(token);
      } 
    } else if (pos == -1) {
      if (this.options.hasShortOption(t)) {
        handleOption(this.options.getOption(t));
      } else if (!getMatchingLongOptions(t).isEmpty()) {
        handleLongOptionWithoutEqual(token);
      } else {
        String opt = getLongPrefix(t);
        if (opt != null && this.options.getOption(opt).acceptsArg()) {
          handleOption(this.options.getOption(opt));
          this.currentOption.addValueForProcessing(stripLeadingAndTrailingQuotesDefaultOff(t.substring(opt.length())));
          this.currentOption = null;
        } else if (isJavaProperty(t)) {
          handleOption(this.options.getOption(t.substring(0, 1)));
          this.currentOption.addValueForProcessing(stripLeadingAndTrailingQuotesDefaultOff(t.substring(1)));
          this.currentOption = null;
        } else {
          handleConcatenatedOptions(token);
        } 
      } 
    } else {
      String opt = t.substring(0, pos);
      String value = t.substring(pos + 1);
      if (opt.length() == 1) {
        Option option = this.options.getOption(opt);
        if (option != null && option.acceptsArg()) {
          handleOption(option);
          this.currentOption.addValueForProcessing(value);
          this.currentOption = null;
        } else {
          handleUnknownToken(token);
        } 
      } else if (isJavaProperty(opt)) {
        handleOption(this.options.getOption(opt.substring(0, 1)));
        this.currentOption.addValueForProcessing(opt.substring(1));
        this.currentOption.addValueForProcessing(value);
        this.currentOption = null;
      } else {
        handleLongOptionWithEqual(token);
      } 
    } 
  }
  
  private void handleToken(String token) throws ParseException {
    this.currentToken = token;
    if (this.skipParsing) {
      this.cmd.addArg(token);
    } else if ("--".equals(token)) {
      this.skipParsing = true;
    } else if (this.currentOption != null && this.currentOption.acceptsArg() && isArgument(token)) {
      this.currentOption.addValueForProcessing(stripLeadingAndTrailingQuotesDefaultOn(token));
    } else if (token.startsWith("--")) {
      handleLongOption(token);
    } else if (token.startsWith("-") && !"-".equals(token)) {
      handleShortAndLongOption(token);
    } else {
      handleUnknownToken(token);
    } 
    if (this.currentOption != null && !this.currentOption.acceptsArg())
      this.currentOption = null; 
  }
  
  private void handleUnknownToken(String token) throws ParseException {
    if (token.startsWith("-") && token.length() > 1 && !this.stopAtNonOption)
      throw new UnrecognizedOptionException("Unrecognized option: " + token, token); 
    this.cmd.addArg(token);
    if (this.stopAtNonOption)
      this.skipParsing = true; 
  }
  
  private boolean isArgument(String token) {
    return (!isOption(token) || isNegativeNumber(token));
  }
  
  private boolean isJavaProperty(String token) {
    String opt = token.isEmpty() ? null : token.substring(0, 1);
    Option option = this.options.getOption(opt);
    return (option != null && (option.getArgs() >= 2 || option.getArgs() == -2));
  }
  
  private boolean isLongOption(String token) {
    if (token == null || !token.startsWith("-") || token.length() == 1)
      return false; 
    int pos = token.indexOf("=");
    String t = (pos == -1) ? token : token.substring(0, pos);
    if (!getMatchingLongOptions(t).isEmpty())
      return true; 
    if (getLongPrefix(token) != null && !token.startsWith("--"))
      return true; 
    return false;
  }
  
  private boolean isNegativeNumber(String token) {
    try {
      Double.parseDouble(token);
      return true;
    } catch (NumberFormatException e) {
      return false;
    } 
  }
  
  private boolean isOption(String token) {
    return (isLongOption(token) || isShortOption(token));
  }
  
  private boolean isShortOption(String token) {
    if (token == null || !token.startsWith("-") || token.length() == 1)
      return false; 
    int pos = token.indexOf("=");
    String optName = (pos == -1) ? token.substring(1) : token.substring(1, pos);
    if (this.options.hasShortOption(optName))
      return true; 
    return (!optName.isEmpty() && this.options.hasShortOption(String.valueOf(optName.charAt(0))));
  }
  
  public CommandLine parse(Options options, String[] arguments) throws ParseException {
    return parse(options, arguments, (Properties)null);
  }
  
  public CommandLine parse(Options options, String[] arguments, boolean stopAtNonOption) throws ParseException {
    return parse(options, arguments, null, stopAtNonOption);
  }
  
  public CommandLine parse(Options options, String[] arguments, Properties properties) throws ParseException {
    return parse(options, arguments, properties, false);
  }
  
  public CommandLine parse(Options options, String[] arguments, Properties properties, boolean stopAtNonOption) throws ParseException {
    this.options = options;
    this.stopAtNonOption = stopAtNonOption;
    this.skipParsing = false;
    this.currentOption = null;
    this.expectedOpts = new ArrayList(options.getRequiredOptions());
    for (OptionGroup group : options.getOptionGroups())
      group.setSelected(null); 
    this.cmd = new CommandLine();
    if (arguments != null)
      for (String argument : arguments)
        handleToken(argument);  
    checkRequiredArgs();
    handleProperties(properties);
    checkRequiredOptions();
    return this.cmd;
  }
  
  private String stripLeadingAndTrailingQuotesDefaultOff(String token) {
    if (this.stripLeadingAndTrailingQuotes != null && this.stripLeadingAndTrailingQuotes.booleanValue())
      return Util.stripLeadingAndTrailingQuotes(token); 
    return token;
  }
  
  private String stripLeadingAndTrailingQuotesDefaultOn(String token) {
    if (this.stripLeadingAndTrailingQuotes == null || this.stripLeadingAndTrailingQuotes.booleanValue())
      return Util.stripLeadingAndTrailingQuotes(token); 
    return token;
  }
  
  private void updateRequiredOptions(Option option) throws AlreadySelectedException {
    if (option.isRequired())
      this.expectedOpts.remove(option.getKey()); 
    if (this.options.getOptionGroup(option) != null) {
      OptionGroup group = this.options.getOptionGroup(option);
      if (group.isRequired())
        this.expectedOpts.remove(group); 
      group.setSelected(option);
    } 
  }
}
