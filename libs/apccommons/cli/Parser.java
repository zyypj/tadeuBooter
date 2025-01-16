package me.syncwrld.booter.libs.apccommons.cli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;

@Deprecated
public abstract class Parser implements CommandLineParser {
  protected CommandLine cmd;
  
  private Options options;
  
  private List requiredOptions;
  
  protected void checkRequiredOptions() throws MissingOptionException {
    if (!getRequiredOptions().isEmpty())
      throw new MissingOptionException(getRequiredOptions()); 
  }
  
  protected abstract String[] flatten(Options paramOptions, String[] paramArrayOfString, boolean paramBoolean) throws ParseException;
  
  protected Options getOptions() {
    return this.options;
  }
  
  protected List getRequiredOptions() {
    return this.requiredOptions;
  }
  
  public CommandLine parse(Options options, String[] arguments) throws ParseException {
    return parse(options, arguments, null, false);
  }
  
  public CommandLine parse(Options options, String[] arguments, boolean stopAtNonOption) throws ParseException {
    return parse(options, arguments, null, stopAtNonOption);
  }
  
  public CommandLine parse(Options options, String[] arguments, Properties properties) throws ParseException {
    return parse(options, arguments, properties, false);
  }
  
  public CommandLine parse(Options options, String[] arguments, Properties properties, boolean stopAtNonOption) throws ParseException {
    for (Option opt : options.helpOptions())
      opt.clearValues(); 
    for (OptionGroup group : options.getOptionGroups())
      group.setSelected(null); 
    setOptions(options);
    this.cmd = new CommandLine();
    boolean eatTheRest = false;
    if (arguments == null)
      arguments = new String[0]; 
    List<String> tokenList = Arrays.asList(flatten(getOptions(), arguments, stopAtNonOption));
    ListIterator<String> iterator = tokenList.listIterator();
    while (iterator.hasNext()) {
      String t = iterator.next();
      if ("--".equals(t)) {
        eatTheRest = true;
      } else if ("-".equals(t)) {
        if (stopAtNonOption) {
          eatTheRest = true;
        } else {
          this.cmd.addArg(t);
        } 
      } else if (t.startsWith("-")) {
        if (stopAtNonOption && !getOptions().hasOption(t)) {
          eatTheRest = true;
          this.cmd.addArg(t);
        } else {
          processOption(t, iterator);
        } 
      } else {
        this.cmd.addArg(t);
        if (stopAtNonOption)
          eatTheRest = true; 
      } 
      if (eatTheRest)
        while (iterator.hasNext()) {
          String str = iterator.next();
          if (!"--".equals(str))
            this.cmd.addArg(str); 
        }  
    } 
    processProperties(properties);
    checkRequiredOptions();
    return this.cmd;
  }
  
  public void processArgs(Option opt, ListIterator<String> iter) throws ParseException {
    while (iter.hasNext()) {
      String str = iter.next();
      if (getOptions().hasOption(str) && str.startsWith("-")) {
        iter.previous();
        break;
      } 
      try {
        opt.addValueForProcessing(Util.stripLeadingAndTrailingQuotes(str));
      } catch (RuntimeException exp) {
        iter.previous();
        break;
      } 
    } 
    if (opt.getValues() == null && !opt.hasOptionalArg())
      throw new MissingArgumentException(opt); 
  }
  
  protected void processOption(String arg, ListIterator<String> iter) throws ParseException {
    boolean hasOption = getOptions().hasOption(arg);
    if (!hasOption)
      throw new UnrecognizedOptionException("Unrecognized option: " + arg, arg); 
    Option opt = (Option)getOptions().getOption(arg).clone();
    updateRequiredOptions(opt);
    if (opt.hasArg())
      processArgs(opt, iter); 
    this.cmd.addOption(opt);
  }
  
  protected void processProperties(Properties properties) throws ParseException {
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
            try {
              opt.addValueForProcessing(value);
            } catch (RuntimeException runtimeException) {} 
        } else if (!"yes".equalsIgnoreCase(value) && !"true".equalsIgnoreCase(value) && !"1".equalsIgnoreCase(value)) {
          continue;
        } 
        this.cmd.addOption(opt);
        updateRequiredOptions(opt);
      } 
    } 
  }
  
  protected void setOptions(Options options) {
    this.options = options;
    this.requiredOptions = new ArrayList(options.getRequiredOptions());
  }
  
  private void updateRequiredOptions(Option opt) throws ParseException {
    if (opt.isRequired())
      getRequiredOptions().remove(opt.getKey()); 
    if (getOptions().getOptionGroup(opt) != null) {
      OptionGroup group = getOptions().getOptionGroup(opt);
      if (group.isRequired())
        getRequiredOptions().remove(group); 
      group.setSelected(opt);
    } 
  }
}
