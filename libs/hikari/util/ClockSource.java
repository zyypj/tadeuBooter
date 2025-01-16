package me.syncwrld.booter.libs.hikari.util;

import java.util.concurrent.TimeUnit;

public interface ClockSource {
  public static final ClockSource CLOCK = Factory.create();
  
  static long currentTime() {
    return CLOCK.currentTime0();
  }
  
  long currentTime0();
  
  static long toMillis(long time) {
    return CLOCK.toMillis0(time);
  }
  
  long toMillis0(long paramLong);
  
  static long toNanos(long time) {
    return CLOCK.toNanos0(time);
  }
  
  long toNanos0(long paramLong);
  
  static long elapsedMillis(long startTime) {
    return CLOCK.elapsedMillis0(startTime);
  }
  
  long elapsedMillis0(long paramLong);
  
  static long elapsedMillis(long startTime, long endTime) {
    return CLOCK.elapsedMillis0(startTime, endTime);
  }
  
  long elapsedMillis0(long paramLong1, long paramLong2);
  
  static long elapsedNanos(long startTime) {
    return CLOCK.elapsedNanos0(startTime);
  }
  
  long elapsedNanos0(long paramLong);
  
  static long elapsedNanos(long startTime, long endTime) {
    return CLOCK.elapsedNanos0(startTime, endTime);
  }
  
  long elapsedNanos0(long paramLong1, long paramLong2);
  
  static long plusMillis(long time, long millis) {
    return CLOCK.plusMillis0(time, millis);
  }
  
  long plusMillis0(long paramLong1, long paramLong2);
  
  static TimeUnit getSourceTimeUnit() {
    return CLOCK.getSourceTimeUnit0();
  }
  
  TimeUnit getSourceTimeUnit0();
  
  static String elapsedDisplayString(long startTime, long endTime) {
    return CLOCK.elapsedDisplayString0(startTime, endTime);
  }
  
  default String elapsedDisplayString0(long startTime, long endTime) {
    long elapsedNanos = elapsedNanos0(startTime, endTime);
    StringBuilder sb = new StringBuilder((elapsedNanos < 0L) ? "-" : "");
    elapsedNanos = Math.abs(elapsedNanos);
    for (TimeUnit unit : TIMEUNITS_DESCENDING) {
      long converted = unit.convert(elapsedNanos, TimeUnit.NANOSECONDS);
      if (converted > 0L) {
        sb.append(converted).append(TIMEUNIT_DISPLAY_VALUES[unit.ordinal()]);
        elapsedNanos -= TimeUnit.NANOSECONDS.convert(converted, unit);
      } 
    } 
    return sb.toString();
  }
  
  public static final TimeUnit[] TIMEUNITS_DESCENDING = new TimeUnit[] { TimeUnit.DAYS, TimeUnit.HOURS, TimeUnit.MINUTES, TimeUnit.SECONDS, TimeUnit.MILLISECONDS, TimeUnit.MICROSECONDS, TimeUnit.NANOSECONDS };
  
  public static final String[] TIMEUNIT_DISPLAY_VALUES = new String[] { "ns", "µs", "ms", "s", "m", "h", "d" };
  
  public static class Factory {
    private static ClockSource create() {
      String os = System.getProperty("os.name");
      if ("Mac OS X".equals(os))
        return new ClockSource.MillisecondClockSource(); 
      return new ClockSource.NanosecondClockSource();
    }
  }
  
  public static final class MillisecondClockSource implements ClockSource {
    public long currentTime0() {
      return System.currentTimeMillis();
    }
    
    public long elapsedMillis0(long startTime) {
      return System.currentTimeMillis() - startTime;
    }
    
    public long elapsedMillis0(long startTime, long endTime) {
      return endTime - startTime;
    }
    
    public long elapsedNanos0(long startTime) {
      return TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis() - startTime);
    }
    
    public long elapsedNanos0(long startTime, long endTime) {
      return TimeUnit.MILLISECONDS.toNanos(endTime - startTime);
    }
    
    public long toMillis0(long time) {
      return time;
    }
    
    public long toNanos0(long time) {
      return TimeUnit.MILLISECONDS.toNanos(time);
    }
    
    public long plusMillis0(long time, long millis) {
      return time + millis;
    }
    
    public TimeUnit getSourceTimeUnit0() {
      return TimeUnit.MILLISECONDS;
    }
  }
  
  public static class NanosecondClockSource implements ClockSource {
    public long currentTime0() {
      return System.nanoTime();
    }
    
    public long toMillis0(long time) {
      return TimeUnit.NANOSECONDS.toMillis(time);
    }
    
    public long toNanos0(long time) {
      return time;
    }
    
    public long elapsedMillis0(long startTime) {
      return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    }
    
    public long elapsedMillis0(long startTime, long endTime) {
      return TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
    }
    
    public long elapsedNanos0(long startTime) {
      return System.nanoTime() - startTime;
    }
    
    public long elapsedNanos0(long startTime, long endTime) {
      return endTime - startTime;
    }
    
    public long plusMillis0(long time, long millis) {
      return time + TimeUnit.MILLISECONDS.toNanos(millis);
    }
    
    public TimeUnit getSourceTimeUnit0() {
      return TimeUnit.NANOSECONDS;
    }
  }
}
