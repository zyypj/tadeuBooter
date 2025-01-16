package me.syncwrld.booter.libs.javax.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.regex.Pattern;
import me.syncwrld.booter.libs.javax.annotation.meta.TypeQualifier;
import me.syncwrld.booter.libs.javax.annotation.meta.TypeQualifierValidator;
import me.syncwrld.booter.libs.javax.annotation.meta.When;

@Documented
@TypeQualifier(applicableTo = String.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface MatchesPattern {
  @RegEx
  String value();
  
  int flags() default 0;
  
  public static class Checker implements TypeQualifierValidator<MatchesPattern> {
    public When forConstantValue(MatchesPattern annotation, Object value) {
      Pattern p = Pattern.compile(annotation.value(), annotation.flags());
      if (p.matcher((String)value).matches())
        return When.ALWAYS; 
      return When.NEVER;
    }
  }
}
