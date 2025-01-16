package me.syncwrld.booter.libs.apccommons.cli;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class CommandLine implements Serializable {
  private static final long serialVersionUID = 1L;
  
  public static final class Builder {
    private final CommandLine commandLine = new CommandLine();
    
    public Builder addArg(String arg) {
      this.commandLine.addArg(arg);
      return this;
    }
    
    public Builder addOption(Option opt) {
      this.commandLine.addOption(opt);
      return this;
    }
    
    public CommandLine build() {
      return this.commandLine;
    }
  }
  
  private final List<String> args = new LinkedList<>();
  
  private final List<Option> options = new ArrayList<>();
  
  protected void addArg(String arg) {
    if (arg != null)
      this.args.add(arg); 
  }
  
  protected void addOption(Option opt) {
    if (opt != null)
      this.options.add(opt); 
  }
  
  public List<String> getArgList() {
    return this.args;
  }
  
  public String[] getArgs() {
    return this.args.<String>toArray(Util.EMPTY_STRING_ARRAY);
  }
  
  @Deprecated
  public Object getOptionObject(char opt) {
    return getOptionObject(String.valueOf(opt));
  }
  
  @Deprecated
  public Object getOptionObject(String opt) {
    try {
      return getParsedOptionValue(opt);
    } catch (ParseException pe) {
      System.err.println("Exception found converting " + opt + " to desired type: " + pe.getMessage());
      return null;
    } 
  }
  
  public Properties getOptionProperties(Option option) {
    Properties props = new Properties();
    for (Option processedOption : this.options) {
      if (processedOption.equals(option)) {
        List<String> values = processedOption.getValuesList();
        if (values.size() >= 2) {
          props.put(values.get(0), values.get(1));
          continue;
        } 
        if (values.size() == 1)
          props.put(values.get(0), "true"); 
      } 
    } 
    return props;
  }
  
  public Properties getOptionProperties(String opt) {
    Properties props = new Properties();
    for (Option option : this.options) {
      if (opt.equals(option.getOpt()) || opt.equals(option.getLongOpt())) {
        List<String> values = option.getValuesList();
        if (values.size() >= 2) {
          props.put(values.get(0), values.get(1));
          continue;
        } 
        if (values.size() == 1)
          props.put(values.get(0), "true"); 
      } 
    } 
    return props;
  }
  
  public Option[] getOptions() {
    return this.options.<Option>toArray(Option.EMPTY_ARRAY);
  }
  
  public String getOptionValue(char opt) {
    return getOptionValue(String.valueOf(opt));
  }
  
  public String getOptionValue(char opt, String defaultValue) {
    return getOptionValue(String.valueOf(opt), defaultValue);
  }
  
  public String getOptionValue(Option option) {
    if (option == null)
      return null; 
    String[] values = getOptionValues(option);
    return (values == null) ? null : values[0];
  }
  
  public String getOptionValue(Option option, String defaultValue) {
    String answer = getOptionValue(option);
    return (answer != null) ? answer : defaultValue;
  }
  
  public String getOptionValue(String opt) {
    return getOptionValue(resolveOption(opt));
  }
  
  public String getOptionValue(String opt, String defaultValue) {
    return getOptionValue(resolveOption(opt), defaultValue);
  }
  
  public String[] getOptionValues(char opt) {
    return getOptionValues(String.valueOf(opt));
  }
  
  public String[] getOptionValues(Option option) {
    List<String> values = new ArrayList<>();
    for (Option processedOption : this.options) {
      if (processedOption.equals(option))
        values.addAll(processedOption.getValuesList()); 
    } 
    return values.isEmpty() ? null : values.<String>toArray(Util.EMPTY_STRING_ARRAY);
  }
  
  public String[] getOptionValues(String opt) {
    return getOptionValues(resolveOption(opt));
  }
  
  public Object getParsedOptionValue(char opt) throws ParseException {
    return getParsedOptionValue(String.valueOf(opt));
  }
  
  public Object getParsedOptionValue(Option option) throws ParseException {
    if (option == null)
      return null; 
    String res = getOptionValue(option);
    if (res == null)
      return null; 
    return TypeHandler.createValue(res, option.getType());
  }
  
  public Object getParsedOptionValue(String opt) throws ParseException {
    return getParsedOptionValue(resolveOption(opt));
  }
  
  public boolean hasOption(char opt) {
    return hasOption(String.valueOf(opt));
  }
  
  public boolean hasOption(Option opt) {
    return this.options.contains(opt);
  }
  
  public boolean hasOption(String opt) {
    return hasOption(resolveOption(opt));
  }
  
  public Iterator<Option> iterator() {
    return this.options.iterator();
  }
  
  private Option resolveOption(String opt) {
    String actual = Util.stripLeadingHyphens(opt);
    if (actual != null)
      for (Option option : this.options) {
        if (actual.equals(option.getOpt()) || actual.equals(option.getLongOpt()))
          return option; 
      }  
    return null;
  }
}
