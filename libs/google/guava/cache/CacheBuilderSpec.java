package me.syncwrld.booter.libs.google.guava.cache;

import java.util.Locale;
import java.util.concurrent.TimeUnit;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.VisibleForTesting;
import me.syncwrld.booter.libs.google.guava.base.MoreObjects;
import me.syncwrld.booter.libs.google.guava.base.Objects;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.guava.base.Splitter;
import me.syncwrld.booter.libs.google.guava.base.Strings;
import me.syncwrld.booter.libs.google.guava.collect.ImmutableList;
import me.syncwrld.booter.libs.google.guava.collect.ImmutableMap;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
public final class CacheBuilderSpec {
  private static final Splitter KEYS_SPLITTER = Splitter.on(',').trimResults();
  
  private static final Splitter KEY_VALUE_SPLITTER = Splitter.on('=').trimResults();
  
  private static final ImmutableMap<String, ValueParser> VALUE_PARSERS = ImmutableMap.builder()
    .put("initialCapacity", new InitialCapacityParser())
    .put("maximumSize", new MaximumSizeParser())
    .put("maximumWeight", new MaximumWeightParser())
    .put("concurrencyLevel", new ConcurrencyLevelParser())
    .put("weakKeys", new KeyStrengthParser(LocalCache.Strength.WEAK))
    .put("softValues", new ValueStrengthParser(LocalCache.Strength.SOFT))
    .put("weakValues", new ValueStrengthParser(LocalCache.Strength.WEAK))
    .put("recordStats", new RecordStatsParser())
    .put("expireAfterAccess", new AccessDurationParser())
    .put("expireAfterWrite", new WriteDurationParser())
    .put("refreshAfterWrite", new RefreshDurationParser())
    .put("refreshInterval", new RefreshDurationParser())
    .buildOrThrow();
  
  @CheckForNull
  @VisibleForTesting
  Integer initialCapacity;
  
  @CheckForNull
  @VisibleForTesting
  Long maximumSize;
  
  @CheckForNull
  @VisibleForTesting
  Long maximumWeight;
  
  @CheckForNull
  @VisibleForTesting
  Integer concurrencyLevel;
  
  @CheckForNull
  @VisibleForTesting
  LocalCache.Strength keyStrength;
  
  @CheckForNull
  @VisibleForTesting
  LocalCache.Strength valueStrength;
  
  @CheckForNull
  @VisibleForTesting
  Boolean recordStats;
  
  @VisibleForTesting
  long writeExpirationDuration;
  
  @CheckForNull
  @VisibleForTesting
  TimeUnit writeExpirationTimeUnit;
  
  @VisibleForTesting
  long accessExpirationDuration;
  
  @CheckForNull
  @VisibleForTesting
  TimeUnit accessExpirationTimeUnit;
  
  @VisibleForTesting
  long refreshDuration;
  
  @CheckForNull
  @VisibleForTesting
  TimeUnit refreshTimeUnit;
  
  private final String specification;
  
  private CacheBuilderSpec(String specification) {
    this.specification = specification;
  }
  
  public static CacheBuilderSpec parse(String cacheBuilderSpecification) {
    CacheBuilderSpec spec = new CacheBuilderSpec(cacheBuilderSpecification);
    if (!cacheBuilderSpecification.isEmpty())
      for (String keyValuePair : KEYS_SPLITTER.split(cacheBuilderSpecification)) {
        ImmutableList<String> immutableList = ImmutableList.copyOf(KEY_VALUE_SPLITTER.split(keyValuePair));
        Preconditions.checkArgument(!immutableList.isEmpty(), "blank key-value pair");
        Preconditions.checkArgument(
            (immutableList.size() <= 2), "key-value pair %s with more than one equals sign", keyValuePair);
        String key = immutableList.get(0);
        ValueParser valueParser = (ValueParser)VALUE_PARSERS.get(key);
        Preconditions.checkArgument((valueParser != null), "unknown key %s", key);
        String value = (immutableList.size() == 1) ? null : immutableList.get(1);
        valueParser.parse(spec, key, value);
      }  
    return spec;
  }
  
  public static CacheBuilderSpec disableCaching() {
    return parse("maximumSize=0");
  }
  
  CacheBuilder<Object, Object> toCacheBuilder() {
    CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder();
    if (this.initialCapacity != null)
      builder.initialCapacity(this.initialCapacity.intValue()); 
    if (this.maximumSize != null)
      builder.maximumSize(this.maximumSize.longValue()); 
    if (this.maximumWeight != null)
      builder.maximumWeight(this.maximumWeight.longValue()); 
    if (this.concurrencyLevel != null)
      builder.concurrencyLevel(this.concurrencyLevel.intValue()); 
    if (this.keyStrength != null)
      switch (this.keyStrength) {
        case WEAK:
          builder.weakKeys();
          break;
        default:
          throw new AssertionError();
      }  
    if (this.valueStrength != null)
      switch (this.valueStrength) {
        case SOFT:
          builder.softValues();
          break;
        case WEAK:
          builder.weakValues();
          break;
        default:
          throw new AssertionError();
      }  
    if (this.recordStats != null && this.recordStats.booleanValue())
      builder.recordStats(); 
    if (this.writeExpirationTimeUnit != null)
      builder.expireAfterWrite(this.writeExpirationDuration, this.writeExpirationTimeUnit); 
    if (this.accessExpirationTimeUnit != null)
      builder.expireAfterAccess(this.accessExpirationDuration, this.accessExpirationTimeUnit); 
    if (this.refreshTimeUnit != null)
      builder.refreshAfterWrite(this.refreshDuration, this.refreshTimeUnit); 
    return builder;
  }
  
  public String toParsableString() {
    return this.specification;
  }
  
  public String toString() {
    return MoreObjects.toStringHelper(this).addValue(toParsableString()).toString();
  }
  
  public int hashCode() {
    return Objects.hashCode(new Object[] { this.initialCapacity, this.maximumSize, this.maximumWeight, this.concurrencyLevel, this.keyStrength, this.valueStrength, this.recordStats, 
          
          durationInNanos(this.writeExpirationDuration, this.writeExpirationTimeUnit), 
          durationInNanos(this.accessExpirationDuration, this.accessExpirationTimeUnit), 
          durationInNanos(this.refreshDuration, this.refreshTimeUnit) });
  }
  
  public boolean equals(@CheckForNull Object obj) {
    if (this == obj)
      return true; 
    if (!(obj instanceof CacheBuilderSpec))
      return false; 
    CacheBuilderSpec that = (CacheBuilderSpec)obj;
    return (Objects.equal(this.initialCapacity, that.initialCapacity) && 
      Objects.equal(this.maximumSize, that.maximumSize) && 
      Objects.equal(this.maximumWeight, that.maximumWeight) && 
      Objects.equal(this.concurrencyLevel, that.concurrencyLevel) && 
      Objects.equal(this.keyStrength, that.keyStrength) && 
      Objects.equal(this.valueStrength, that.valueStrength) && 
      Objects.equal(this.recordStats, that.recordStats) && 
      Objects.equal(
        durationInNanos(this.writeExpirationDuration, this.writeExpirationTimeUnit), 
        durationInNanos(that.writeExpirationDuration, that.writeExpirationTimeUnit)) && 
      Objects.equal(
        durationInNanos(this.accessExpirationDuration, this.accessExpirationTimeUnit), 
        durationInNanos(that.accessExpirationDuration, that.accessExpirationTimeUnit)) && 
      Objects.equal(
        durationInNanos(this.refreshDuration, this.refreshTimeUnit), 
        durationInNanos(that.refreshDuration, that.refreshTimeUnit)));
  }
  
  @CheckForNull
  private static Long durationInNanos(long duration, @CheckForNull TimeUnit unit) {
    return (unit == null) ? null : Long.valueOf(unit.toNanos(duration));
  }
  
  static abstract class IntegerParser implements ValueParser {
    protected abstract void parseInteger(CacheBuilderSpec param1CacheBuilderSpec, int param1Int);
    
    public void parse(CacheBuilderSpec spec, String key, String value) {
      if (Strings.isNullOrEmpty(value))
        throw new IllegalArgumentException("value of key " + key + " omitted"); 
      try {
        parseInteger(spec, Integer.parseInt(value));
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException(CacheBuilderSpec
            .format("key %s value set to %s, must be integer", new Object[] { key, value }), e);
      } 
    }
  }
  
  static abstract class LongParser implements ValueParser {
    protected abstract void parseLong(CacheBuilderSpec param1CacheBuilderSpec, long param1Long);
    
    public void parse(CacheBuilderSpec spec, String key, String value) {
      if (Strings.isNullOrEmpty(value))
        throw new IllegalArgumentException("value of key " + key + " omitted"); 
      try {
        parseLong(spec, Long.parseLong(value));
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException(CacheBuilderSpec
            .format("key %s value set to %s, must be integer", new Object[] { key, value }), e);
      } 
    }
  }
  
  static class InitialCapacityParser extends IntegerParser {
    protected void parseInteger(CacheBuilderSpec spec, int value) {
      Preconditions.checkArgument((spec.initialCapacity == null), "initial capacity was already set to %s", spec.initialCapacity);
      spec.initialCapacity = Integer.valueOf(value);
    }
  }
  
  static class MaximumSizeParser extends LongParser {
    protected void parseLong(CacheBuilderSpec spec, long value) {
      Preconditions.checkArgument((spec.maximumSize == null), "maximum size was already set to %s", spec.maximumSize);
      Preconditions.checkArgument((spec.maximumWeight == null), "maximum weight was already set to %s", spec.maximumWeight);
      spec.maximumSize = Long.valueOf(value);
    }
  }
  
  static class MaximumWeightParser extends LongParser {
    protected void parseLong(CacheBuilderSpec spec, long value) {
      Preconditions.checkArgument((spec.maximumWeight == null), "maximum weight was already set to %s", spec.maximumWeight);
      Preconditions.checkArgument((spec.maximumSize == null), "maximum size was already set to %s", spec.maximumSize);
      spec.maximumWeight = Long.valueOf(value);
    }
  }
  
  static class ConcurrencyLevelParser extends IntegerParser {
    protected void parseInteger(CacheBuilderSpec spec, int value) {
      Preconditions.checkArgument((spec.concurrencyLevel == null), "concurrency level was already set to %s", spec.concurrencyLevel);
      spec.concurrencyLevel = Integer.valueOf(value);
    }
  }
  
  static class KeyStrengthParser implements ValueParser {
    private final LocalCache.Strength strength;
    
    public KeyStrengthParser(LocalCache.Strength strength) {
      this.strength = strength;
    }
    
    public void parse(CacheBuilderSpec spec, String key, @CheckForNull String value) {
      Preconditions.checkArgument((value == null), "key %s does not take values", key);
      Preconditions.checkArgument((spec.keyStrength == null), "%s was already set to %s", key, spec.keyStrength);
      spec.keyStrength = this.strength;
    }
  }
  
  static class ValueStrengthParser implements ValueParser {
    private final LocalCache.Strength strength;
    
    public ValueStrengthParser(LocalCache.Strength strength) {
      this.strength = strength;
    }
    
    public void parse(CacheBuilderSpec spec, String key, @CheckForNull String value) {
      Preconditions.checkArgument((value == null), "key %s does not take values", key);
      Preconditions.checkArgument((spec.valueStrength == null), "%s was already set to %s", key, spec.valueStrength);
      spec.valueStrength = this.strength;
    }
  }
  
  static class RecordStatsParser implements ValueParser {
    public void parse(CacheBuilderSpec spec, String key, @CheckForNull String value) {
      Preconditions.checkArgument((value == null), "recordStats does not take values");
      Preconditions.checkArgument((spec.recordStats == null), "recordStats already set");
      spec.recordStats = Boolean.valueOf(true);
    }
  }
  
  static abstract class DurationParser implements ValueParser {
    protected abstract void parseDuration(CacheBuilderSpec param1CacheBuilderSpec, long param1Long, TimeUnit param1TimeUnit);
    
    public void parse(CacheBuilderSpec spec, String key, @CheckForNull String value) {
      if (Strings.isNullOrEmpty(value))
        throw new IllegalArgumentException("value of key " + key + " omitted"); 
      try {
        TimeUnit timeUnit;
        char lastChar = value.charAt(value.length() - 1);
        switch (lastChar) {
          case 'd':
            timeUnit = TimeUnit.DAYS;
            break;
          case 'h':
            timeUnit = TimeUnit.HOURS;
            break;
          case 'm':
            timeUnit = TimeUnit.MINUTES;
            break;
          case 's':
            timeUnit = TimeUnit.SECONDS;
            break;
          default:
            throw new IllegalArgumentException(CacheBuilderSpec
                .format("key %s invalid unit: was %s, must end with one of [dhms]", new Object[] { key, value }));
        } 
        long duration = Long.parseLong(value.substring(0, value.length() - 1));
        parseDuration(spec, duration, timeUnit);
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException(CacheBuilderSpec
            .format("key %s value set to %s, must be integer", new Object[] { key, value }));
      } 
    }
  }
  
  static class AccessDurationParser extends DurationParser {
    protected void parseDuration(CacheBuilderSpec spec, long duration, TimeUnit unit) {
      Preconditions.checkArgument((spec.accessExpirationTimeUnit == null), "expireAfterAccess already set");
      spec.accessExpirationDuration = duration;
      spec.accessExpirationTimeUnit = unit;
    }
  }
  
  static class WriteDurationParser extends DurationParser {
    protected void parseDuration(CacheBuilderSpec spec, long duration, TimeUnit unit) {
      Preconditions.checkArgument((spec.writeExpirationTimeUnit == null), "expireAfterWrite already set");
      spec.writeExpirationDuration = duration;
      spec.writeExpirationTimeUnit = unit;
    }
  }
  
  static class RefreshDurationParser extends DurationParser {
    protected void parseDuration(CacheBuilderSpec spec, long duration, TimeUnit unit) {
      Preconditions.checkArgument((spec.refreshTimeUnit == null), "refreshAfterWrite already set");
      spec.refreshDuration = duration;
      spec.refreshTimeUnit = unit;
    }
  }
  
  private static String format(String format, Object... args) {
    return String.format(Locale.ROOT, format, args);
  }
  
  private static interface ValueParser {
    void parse(CacheBuilderSpec param1CacheBuilderSpec, String param1String1, @CheckForNull String param1String2);
  }
}
