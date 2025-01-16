package me.syncwrld.booter.libs.apccommons.cli;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class OptionGroup implements Serializable {
  private static final long serialVersionUID = 1L;
  
  private final Map<String, Option> optionMap = new LinkedHashMap<>();
  
  private String selected;
  
  private boolean required;
  
  public OptionGroup addOption(Option option) {
    this.optionMap.put(option.getKey(), option);
    return this;
  }
  
  public Collection<String> getNames() {
    return this.optionMap.keySet();
  }
  
  public Collection<Option> getOptions() {
    return this.optionMap.values();
  }
  
  public String getSelected() {
    return this.selected;
  }
  
  public boolean isRequired() {
    return this.required;
  }
  
  public void setRequired(boolean required) {
    this.required = required;
  }
  
  public void setSelected(Option option) throws AlreadySelectedException {
    if (option == null) {
      this.selected = null;
      return;
    } 
    if (this.selected != null && !this.selected.equals(option.getKey()))
      throw new AlreadySelectedException(this, option); 
    this.selected = option.getKey();
  }
  
  public String toString() {
    StringBuilder buff = new StringBuilder();
    Iterator<Option> iter = getOptions().iterator();
    buff.append("[");
    while (iter.hasNext()) {
      Option option = iter.next();
      if (option.getOpt() != null) {
        buff.append("-");
        buff.append(option.getOpt());
      } else {
        buff.append("--");
        buff.append(option.getLongOpt());
      } 
      if (option.getDescription() != null) {
        buff.append(" ");
        buff.append(option.getDescription());
      } 
      if (iter.hasNext())
        buff.append(", "); 
    } 
    buff.append("]");
    return buff.toString();
  }
}
