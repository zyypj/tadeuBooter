package me.syncwrld.booter.libs.javax.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import me.syncwrld.booter.libs.javax.annotation.meta.TypeQualifierNickname;
import me.syncwrld.booter.libs.javax.annotation.meta.TypeQualifierValidator;
import me.syncwrld.booter.libs.javax.annotation.meta.When;

@Documented
@Syntax("RegEx")
@Retention(RetentionPolicy.RUNTIME)
@TypeQualifierNickname
public @interface RegEx {
  When when() default When.ALWAYS;
  
  public static class Checker implements TypeQualifierValidator<RegEx> {
    public When forConstantValue(RegEx annotation, Object value) {
      if (!(value instanceof String))
        return When.NEVER; 
      try {
        Pattern.compile((String)value);
      } catch (PatternSyntaxException e) {
        return When.NEVER;
      } 
      return When.ALWAYS;
    }
  }
}
