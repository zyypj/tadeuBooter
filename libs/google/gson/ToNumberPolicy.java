package me.syncwrld.booter.libs.google.gson;

import java.io.IOException;
import java.math.BigDecimal;
import me.syncwrld.booter.libs.google.gson.internal.LazilyParsedNumber;
import me.syncwrld.booter.libs.google.gson.stream.JsonReader;
import me.syncwrld.booter.libs.google.gson.stream.MalformedJsonException;

public enum ToNumberPolicy implements ToNumberStrategy {
  DOUBLE {
    public Double readNumber(JsonReader in) throws IOException {
      return Double.valueOf(in.nextDouble());
    }
  },
  LAZILY_PARSED_NUMBER {
    public Number readNumber(JsonReader in) throws IOException {
      return (Number)new LazilyParsedNumber(in.nextString());
    }
  },
  LONG_OR_DOUBLE {
    public Number readNumber(JsonReader in) throws IOException, JsonParseException {
      String value = in.nextString();
      try {
        return Long.valueOf(Long.parseLong(value));
      } catch (NumberFormatException longE) {
        try {
          Double d = Double.valueOf(value);
          if ((d.isInfinite() || d.isNaN()) && !in.isLenient())
            throw new MalformedJsonException("JSON forbids NaN and infinities: " + d + "; at path " + in.getPreviousPath()); 
          return d;
        } catch (NumberFormatException doubleE) {
          throw new JsonParseException("Cannot parse " + value + "; at path " + in.getPreviousPath(), doubleE);
        } 
      } 
    }
  },
  BIG_DECIMAL {
    public BigDecimal readNumber(JsonReader in) throws IOException {
      String value = in.nextString();
      try {
        return new BigDecimal(value);
      } catch (NumberFormatException e) {
        throw new JsonParseException("Cannot parse " + value + "; at path " + in.getPreviousPath(), e);
      } 
    }
  };
}
