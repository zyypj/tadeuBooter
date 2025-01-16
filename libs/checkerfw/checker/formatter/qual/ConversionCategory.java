package me.syncwrld.booter.libs.checkerfw.checker.formatter.qual;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import me.syncwrld.booter.libs.checkerfw.dataflow.qual.Pure;

public enum ConversionCategory {
  GENERAL("bBhHsS", (Class[])null),
  CHAR("cC", new Class[] { Character.class, Byte.class, Short.class, Integer.class }),
  INT("doxX", new Class[] { Byte.class, Short.class, Integer.class, Long.class, BigInteger.class }),
  FLOAT("eEfgGaA", new Class[] { Float.class, Double.class, BigDecimal.class }),
  TIME("tT", new Class[] { Long.class, Calendar.class, Date.class }),
  CHAR_AND_INT(null, new Class[] { Byte.class, Short.class, Integer.class }),
  INT_AND_TIME(null, new Class[] { Long.class }),
  NULL(null, new Class[0]),
  UNUSED(null, (Class[])null);
  
  public final Class<?>[] types;
  
  public final String chars;
  
  private static final ConversionCategory[] conversionCategoriesWithChar;
  
  private static final ConversionCategory[] conversionCategoriesForIntersect;
  
  private static final ConversionCategory[] conversionCategoriesForUnion;
  
  ConversionCategory(String chars, Class<?>... types) {
    this.chars = chars;
    if (types == null) {
      this.types = types;
    } else {
      List<Class<?>> typesWithPrimitives = new ArrayList<>(types.length);
      for (Class<?> type : types) {
        typesWithPrimitives.add(type);
        Class<?> unwrapped = unwrapPrimitive(type);
        if (unwrapped != null)
          typesWithPrimitives.add(unwrapped); 
      } 
      this.types = (Class[])typesWithPrimitives.<Class<?>[]>toArray((Class<?>[][])new Class[typesWithPrimitives.size()]);
    } 
  }
  
  private static Class<? extends Object> unwrapPrimitive(Class<?> c) {
    if (c == Byte.class)
      return (Class)byte.class; 
    if (c == Character.class)
      return (Class)char.class; 
    if (c == Short.class)
      return (Class)short.class; 
    if (c == Integer.class)
      return (Class)int.class; 
    if (c == Long.class)
      return (Class)long.class; 
    if (c == Float.class)
      return (Class)float.class; 
    if (c == Double.class)
      return (Class)double.class; 
    if (c == Boolean.class)
      return (Class)boolean.class; 
    return null;
  }
  
  static {
    conversionCategoriesWithChar = new ConversionCategory[] { GENERAL, CHAR, INT, FLOAT, TIME };
    conversionCategoriesForIntersect = new ConversionCategory[] { CHAR, INT, FLOAT, TIME, CHAR_AND_INT, INT_AND_TIME, NULL };
    conversionCategoriesForUnion = new ConversionCategory[] { NULL, CHAR_AND_INT, INT_AND_TIME, CHAR, INT, FLOAT, TIME };
  }
  
  public static ConversionCategory fromConversionChar(char c) {
    for (ConversionCategory v : conversionCategoriesWithChar) {
      if (v.chars.contains(String.valueOf(c)))
        return v; 
    } 
    throw new IllegalArgumentException("Bad conversion character " + c);
  }
  
  private static <E> Set<E> arrayToSet(E[] a) {
    return new HashSet<>(Arrays.asList(a));
  }
  
  public static boolean isSubsetOf(ConversionCategory a, ConversionCategory b) {
    return (intersect(a, b) == a);
  }
  
  public static ConversionCategory intersect(ConversionCategory a, ConversionCategory b) {
    if (a == UNUSED)
      return b; 
    if (b == UNUSED)
      return a; 
    if (a == GENERAL)
      return b; 
    if (b == GENERAL)
      return a; 
    Set<Class<?>> as = arrayToSet(a.types);
    Set<Class<?>> bs = arrayToSet(b.types);
    as.retainAll(bs);
    for (ConversionCategory v : conversionCategoriesForIntersect) {
      Set<Class<?>> vs = arrayToSet(v.types);
      if (vs.equals(as))
        return v; 
    } 
    throw new RuntimeException();
  }
  
  public static ConversionCategory union(ConversionCategory a, ConversionCategory b) {
    if (a == UNUSED || b == UNUSED)
      return UNUSED; 
    if (a == GENERAL || b == GENERAL)
      return GENERAL; 
    if ((a == CHAR_AND_INT && b == INT_AND_TIME) || (a == INT_AND_TIME && b == CHAR_AND_INT))
      return INT; 
    Set<Class<?>> as = arrayToSet(a.types);
    Set<Class<?>> bs = arrayToSet(b.types);
    as.addAll(bs);
    for (ConversionCategory v : conversionCategoriesForUnion) {
      Set<Class<?>> vs = arrayToSet(v.types);
      if (vs.equals(as))
        return v; 
    } 
    return GENERAL;
  }
  
  public boolean isAssignableFrom(Class<?> argType) {
    if (this.types == null)
      return true; 
    if (argType == void.class)
      return true; 
    for (Class<?> c : this.types) {
      if (c.isAssignableFrom(argType))
        return true; 
    } 
    return false;
  }
  
  @Pure
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(name());
    sb.append(" conversion category");
    if (this.types == null || this.types.length == 0)
      return sb.toString(); 
    StringJoiner sj = new StringJoiner(", ", "(one of: ", ")");
    for (Class<?> cls : this.types)
      sj.add(cls.getSimpleName()); 
    sb.append(" ");
    sb.append(sj);
    return sb.toString();
  }
}
