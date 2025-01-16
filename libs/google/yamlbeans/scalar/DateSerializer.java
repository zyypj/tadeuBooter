package me.syncwrld.booter.libs.google.yamlbeans.scalar;

import java.text.ParseException;
import java.util.Date;
import me.syncwrld.booter.libs.google.yamlbeans.YamlException;

public class DateSerializer implements ScalarSerializer<Date> {
  private DateTimeParser dateParser = new DateTimeParser();
  
  public Date read(String value) throws YamlException {
    try {
      return this.dateParser.parse(value);
    } catch (ParseException ex) {
      throw new YamlException("Invalid date: " + value, ex);
    } 
  }
  
  public String write(Date object) throws YamlException {
    return this.dateParser.format(object);
  }
}
