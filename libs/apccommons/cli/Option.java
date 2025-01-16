package me.syncwrld.booter.libs.apccommons.cli;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Option implements Cloneable, Serializable {
  public static final int UNINITIALIZED = -1;
  
  public static final int UNLIMITED_VALUES = -2;
  
  private static final long serialVersionUID = 1L;
  
  public static final class Builder {
    private String option;
    
    private String description;
    
    private String longOption;
    
    private String argName;
    
    private boolean required;
    
    private boolean optionalArg;
    
    private int argCount = -1;
    
    private Class<?> type = String.class;
    
    private char valueSeparator;
    
    private Builder(String option) throws IllegalArgumentException {
      option(option);
    }
    
    public Builder argName(String argName) {
      this.argName = argName;
      return this;
    }
    
    public Option build() {
      if (this.option == null && this.longOption == null)
        throw new IllegalArgumentException("Either opt or longOpt must be specified"); 
      return new Option(this);
    }
    
    public Builder desc(String description) {
      this.description = description;
      return this;
    }
    
    public Builder hasArg() {
      return hasArg(true);
    }
    
    public Builder hasArg(boolean hasArg) {
      this.argCount = hasArg ? 1 : -1;
      return this;
    }
    
    public Builder hasArgs() {
      this.argCount = -2;
      return this;
    }
    
    public Builder longOpt(String longOpt) {
      this.longOption = longOpt;
      return this;
    }
    
    public Builder numberOfArgs(int argCount) {
      this.argCount = argCount;
      return this;
    }
    
    public Builder option(String option) throws IllegalArgumentException {
      this.option = OptionValidator.validate(option);
      return this;
    }
    
    public Builder optionalArg(boolean optionalArg) {
      this.argCount = optionalArg ? 1 : -1;
      this.optionalArg = optionalArg;
      return this;
    }
    
    public Builder required() {
      return required(true);
    }
    
    public Builder required(boolean required) {
      this.required = required;
      return this;
    }
    
    public Builder type(Class<?> type) {
      this.type = type;
      return this;
    }
    
    public Builder valueSeparator() {
      return valueSeparator('=');
    }
    
    public Builder valueSeparator(char valueSeparator) {
      this.valueSeparator = valueSeparator;
      return this;
    }
  }
  
  static final Option[] EMPTY_ARRAY = new Option[0];
  
  private final String option;
  
  private String longOption;
  
  private String argName;
  
  private String description;
  
  private boolean required;
  
  private boolean optionalArg;
  
  public static Builder builder() {
    return builder(null);
  }
  
  public static Builder builder(String option) {
    return new Builder(option);
  }
  
  private int argCount = -1;
  
  private Class<?> type = String.class;
  
  private List<String> values = new ArrayList<>();
  
  private char valuesep;
  
  private Option(Builder builder) {
    this.argName = builder.argName;
    this.description = builder.description;
    this.longOption = builder.longOption;
    this.argCount = builder.argCount;
    this.option = builder.option;
    this.optionalArg = builder.optionalArg;
    this.required = builder.required;
    this.type = builder.type;
    this.valuesep = builder.valueSeparator;
  }
  
  public Option(String option, boolean hasArg, String description) throws IllegalArgumentException {
    this(option, null, hasArg, description);
  }
  
  public Option(String option, String description) throws IllegalArgumentException {
    this(option, null, false, description);
  }
  
  public Option(String option, String longOption, boolean hasArg, String description) throws IllegalArgumentException {
    this.option = OptionValidator.validate(option);
    this.longOption = longOption;
    if (hasArg)
      this.argCount = 1; 
    this.description = description;
  }
  
  boolean acceptsArg() {
    return ((hasArg() || hasArgs() || hasOptionalArg()) && (this.argCount <= 0 || this.values.size() < this.argCount));
  }
  
  private void add(String value) {
    if (!acceptsArg())
      throw new IllegalArgumentException("Cannot add value, list full."); 
    this.values.add(value);
  }
  
  @Deprecated
  public boolean addValue(String value) {
    throw new UnsupportedOperationException("The addValue method is not intended for client use. Subclasses should use the addValueForProcessing method instead. ");
  }
  
  void addValueForProcessing(String value) {
    if (this.argCount == -1)
      throw new IllegalArgumentException("NO_ARGS_ALLOWED"); 
    processValue(value);
  }
  
  void clearValues() {
    this.values.clear();
  }
  
  public Object clone() {
    try {
      Option option = (Option)super.clone();
      option.values = new ArrayList<>(this.values);
      return option;
    } catch (CloneNotSupportedException e) {
      throw new UnsupportedOperationException(e.getMessage(), e);
    } 
  }
  
  public boolean equals(Object obj) {
    if (this == obj)
      return true; 
    if (!(obj instanceof Option))
      return false; 
    Option other = (Option)obj;
    return (Objects.equals(this.longOption, other.longOption) && Objects.equals(this.option, other.option));
  }
  
  public String getArgName() {
    return this.argName;
  }
  
  public int getArgs() {
    return this.argCount;
  }
  
  public String getDescription() {
    return this.description;
  }
  
  public int getId() {
    return getKey().charAt(0);
  }
  
  String getKey() {
    return (this.option == null) ? this.longOption : this.option;
  }
  
  public String getLongOpt() {
    return this.longOption;
  }
  
  public String getOpt() {
    return this.option;
  }
  
  public Object getType() {
    return this.type;
  }
  
  public String getValue() {
    return hasNoValues() ? null : this.values.get(0);
  }
  
  public String getValue(int index) throws IndexOutOfBoundsException {
    return hasNoValues() ? null : this.values.get(index);
  }
  
  public String getValue(String defaultValue) {
    String value = getValue();
    return (value != null) ? value : defaultValue;
  }
  
  public String[] getValues() {
    return hasNoValues() ? null : this.values.<String>toArray(Util.EMPTY_STRING_ARRAY);
  }
  
  public char getValueSeparator() {
    return this.valuesep;
  }
  
  public List<String> getValuesList() {
    return this.values;
  }
  
  public boolean hasArg() {
    return (this.argCount > 0 || this.argCount == -2);
  }
  
  public boolean hasArgName() {
    return (this.argName != null && !this.argName.isEmpty());
  }
  
  public boolean hasArgs() {
    return (this.argCount > 1 || this.argCount == -2);
  }
  
  public int hashCode() {
    return Objects.hash(new Object[] { this.longOption, this.option });
  }
  
  public boolean hasLongOpt() {
    return (this.longOption != null);
  }
  
  private boolean hasNoValues() {
    return this.values.isEmpty();
  }
  
  public boolean hasOptionalArg() {
    return this.optionalArg;
  }
  
  public boolean hasValueSeparator() {
    return (this.valuesep > '\000');
  }
  
  public boolean isRequired() {
    return this.required;
  }
  
  private void processValue(String value) {
    if (hasValueSeparator()) {
      char sep = getValueSeparator();
      int index = value.indexOf(sep);
      while (index != -1) {
        if (this.values.size() == this.argCount - 1)
          break; 
        add(value.substring(0, index));
        value = value.substring(index + 1);
        index = value.indexOf(sep);
      } 
    } 
    add(value);
  }
  
  boolean requiresArg() {
    if (this.optionalArg)
      return false; 
    if (this.argCount == -2)
      return this.values.isEmpty(); 
    return acceptsArg();
  }
  
  public void setArgName(String argName) {
    this.argName = argName;
  }
  
  public void setArgs(int num) {
    this.argCount = num;
  }
  
  public void setDescription(String description) {
    this.description = description;
  }
  
  public void setLongOpt(String longOpt) {
    this.longOption = longOpt;
  }
  
  public void setOptionalArg(boolean optionalArg) {
    this.optionalArg = optionalArg;
  }
  
  public void setRequired(boolean required) {
    this.required = required;
  }
  
  public void setType(Class<?> type) {
    this.type = type;
  }
  
  @Deprecated
  public void setType(Object type) {
    setType((Class)type);
  }
  
  public void setValueSeparator(char sep) {
    this.valuesep = sep;
  }
  
  public String toString() {
    StringBuilder buf = (new StringBuilder()).append("[ option: ");
    buf.append(this.option);
    if (this.longOption != null)
      buf.append(" ").append(this.longOption); 
    buf.append(" ");
    if (hasArgs()) {
      buf.append("[ARG...]");
    } else if (hasArg()) {
      buf.append(" [ARG]");
    } 
    buf.append(" :: ").append(this.description);
    if (this.type != null)
      buf.append(" :: ").append(this.type); 
    buf.append(" ]");
    return buf.toString();
  }
}
