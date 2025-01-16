package me.syncwrld.booter.libs.apccommons.cli;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Options implements Serializable {
  private static final long serialVersionUID = 1L;
  
  private final Map<String, Option> shortOpts = new LinkedHashMap<>();
  
  private final Map<String, Option> longOpts = new LinkedHashMap<>();
  
  private final List<Object> requiredOpts = new ArrayList();
  
  private final Map<String, OptionGroup> optionGroups = new LinkedHashMap<>();
  
  public Options addOption(Option opt) {
    String key = opt.getKey();
    if (opt.hasLongOpt())
      this.longOpts.put(opt.getLongOpt(), opt); 
    if (opt.isRequired()) {
      if (this.requiredOpts.contains(key))
        this.requiredOpts.remove(this.requiredOpts.indexOf(key)); 
      this.requiredOpts.add(key);
    } 
    this.shortOpts.put(key, opt);
    return this;
  }
  
  public Options addOption(String opt, boolean hasArg, String description) {
    addOption(opt, null, hasArg, description);
    return this;
  }
  
  public Options addOption(String opt, String description) {
    addOption(opt, null, false, description);
    return this;
  }
  
  public Options addOption(String opt, String longOpt, boolean hasArg, String description) {
    addOption(new Option(opt, longOpt, hasArg, description));
    return this;
  }
  
  public Options addOptionGroup(OptionGroup group) {
    if (group.isRequired())
      this.requiredOpts.add(group); 
    for (Option option : group.getOptions()) {
      option.setRequired(false);
      addOption(option);
      this.optionGroups.put(option.getKey(), group);
    } 
    return this;
  }
  
  public Options addRequiredOption(String opt, String longOpt, boolean hasArg, String description) {
    Option option = new Option(opt, longOpt, hasArg, description);
    option.setRequired(true);
    addOption(option);
    return this;
  }
  
  public List<String> getMatchingOptions(String opt) {
    opt = Util.stripLeadingHyphens(opt);
    List<String> matchingOpts = new ArrayList<>();
    if (this.longOpts.containsKey(opt))
      return Collections.singletonList(opt); 
    for (String longOpt : this.longOpts.keySet()) {
      if (longOpt.startsWith(opt))
        matchingOpts.add(longOpt); 
    } 
    return matchingOpts;
  }
  
  public Option getOption(String opt) {
    opt = Util.stripLeadingHyphens(opt);
    Option option = this.shortOpts.get(opt);
    return (option != null) ? option : this.longOpts.get(opt);
  }
  
  public OptionGroup getOptionGroup(Option opt) {
    return this.optionGroups.get(opt.getKey());
  }
  
  Collection<OptionGroup> getOptionGroups() {
    return new HashSet<>(this.optionGroups.values());
  }
  
  public Collection<Option> getOptions() {
    return Collections.unmodifiableCollection(helpOptions());
  }
  
  public List getRequiredOptions() {
    return Collections.unmodifiableList(this.requiredOpts);
  }
  
  public boolean hasLongOption(String opt) {
    opt = Util.stripLeadingHyphens(opt);
    return this.longOpts.containsKey(opt);
  }
  
  public boolean hasOption(String opt) {
    opt = Util.stripLeadingHyphens(opt);
    return (this.shortOpts.containsKey(opt) || this.longOpts.containsKey(opt));
  }
  
  public boolean hasShortOption(String opt) {
    opt = Util.stripLeadingHyphens(opt);
    return this.shortOpts.containsKey(opt);
  }
  
  List<Option> helpOptions() {
    return new ArrayList<>(this.shortOpts.values());
  }
  
  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append("[ Options: [ short ");
    buf.append(this.shortOpts.toString());
    buf.append(" ] [ long ");
    buf.append(this.longOpts);
    buf.append(" ]");
    return buf.toString();
  }
}
