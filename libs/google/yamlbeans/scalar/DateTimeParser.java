package me.syncwrld.booter.libs.google.yamlbeans.scalar;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

class DateTimeParser extends DateFormat {
  private static final String DATEFORMAT_YAML = "yyyy-MM-dd HH:mm:ss";
  
  private static final int FORMAT_NONE = -1;
  
  private SimpleDateFormat outputFormat;
  
  private ArrayList<Parser> parsers = new ArrayList<Parser>();
  
  public DateTimeParser() {
    this.outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    this.parsers.add(new SimpleParser(this.outputFormat));
    this.parsers.add(new Parser() {
          public Date parse(String s) throws ParseException {
            try {
              long val = Long.parseLong(s);
              return new Date(val);
            } catch (NumberFormatException e) {
              throw new ParseException("Error parsing value", -1);
            } 
          }
        });
    this.parsers.add(new SimpleParser("yyyy-MM-dd"));
    this.parsers.add(new SimpleParser(0, 0));
    this.parsers.add(new SimpleParser(1, 1));
    this.parsers.add(new SimpleParser(2, 2));
    this.parsers.add(new SimpleParser(3, 3));
    this.parsers.add(new SimpleParser(0, -1));
    this.parsers.add(new SimpleParser(1, -1));
    this.parsers.add(new SimpleParser(2, -1));
    this.parsers.add(new SimpleParser(3, -1));
    this.parsers.add(new SimpleParser(-1, 0));
    this.parsers.add(new SimpleParser(-1, 1));
    this.parsers.add(new SimpleParser(-1, 2));
    this.parsers.add(new SimpleParser(-1, 3));
  }
  
  public Date parse(String text, ParsePosition pos) {
    String s = text.substring(pos.getIndex());
    Date date = null;
    for (Parser parser : this.parsers) {
      try {
        date = parser.parse(s);
        break;
      } catch (ParseException parseException) {}
    } 
    if (date == null) {
      pos.setIndex(pos.getIndex());
      pos.setErrorIndex(pos.getIndex());
    } else {
      pos.setIndex(s.length());
    } 
    return date;
  }
  
  public StringBuffer format(Date date, StringBuffer buf, FieldPosition pos) {
    return this.outputFormat.format(date, buf, pos);
  }
  
  protected static interface Parser {
    Date parse(String param1String) throws ParseException;
  }
  
  protected static class SimpleParser implements Parser {
    private DateFormat format;
    
    public SimpleParser(String format) {
      this.format = new SimpleDateFormat(format);
    }
    
    public SimpleParser(DateFormat format) {
      this.format = format;
    }
    
    public SimpleParser(int dateType, int timeType) {
      if (timeType < 0) {
        this.format = DateFormat.getDateInstance(dateType);
      } else if (dateType < 0) {
        this.format = DateFormat.getTimeInstance(timeType);
      } else {
        this.format = DateFormat.getDateTimeInstance(dateType, timeType);
      } 
    }
    
    public Date parse(String s) throws ParseException {
      return this.format.parse(s);
    }
  }
}
