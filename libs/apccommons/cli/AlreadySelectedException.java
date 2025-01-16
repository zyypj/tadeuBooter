package me.syncwrld.booter.libs.apccommons.cli;

public class AlreadySelectedException extends ParseException {
  private static final long serialVersionUID = 3674381532418544760L;
  
  private final OptionGroup group;
  
  private final Option option;
  
  public AlreadySelectedException(OptionGroup group, Option option) {
    this("The option '" + option.getKey() + "' was specified but an option from this group has already been selected: '" + group.getSelected() + "'", group, option);
  }
  
  public AlreadySelectedException(String message) {
    this(message, null, null);
  }
  
  private AlreadySelectedException(String message, OptionGroup group, Option option) {
    super(message);
    this.group = group;
    this.option = option;
  }
  
  public Option getOption() {
    return this.option;
  }
  
  public OptionGroup getOptionGroup() {
    return this.group;
  }
}
