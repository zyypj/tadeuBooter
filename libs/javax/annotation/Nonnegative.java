package me.syncwrld.booter.libs.javax.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import me.syncwrld.booter.libs.javax.annotation.meta.TypeQualifier;
import me.syncwrld.booter.libs.javax.annotation.meta.TypeQualifierValidator;
import me.syncwrld.booter.libs.javax.annotation.meta.When;

@Documented
@TypeQualifier(applicableTo = Number.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface Nonnegative {
  When when() default When.ALWAYS;
  
  public static class Checker implements TypeQualifierValidator<Nonnegative> {
    public When forConstantValue(Nonnegative annotation, Object v) {
      boolean isNegative;
      if (!(v instanceof Number))
        return When.NEVER; 
      Number value = (Number)v;
      if (value instanceof Long) {
        isNegative = (value.longValue() < 0L);
      } else if (value instanceof Double) {
        isNegative = (value.doubleValue() < 0.0D);
      } else if (value instanceof Float) {
        isNegative = (value.floatValue() < 0.0F);
      } else {
        isNegative = (value.intValue() < 0);
      } 
      if (isNegative)
        return When.NEVER; 
      return When.ALWAYS;
    }
  }
}
