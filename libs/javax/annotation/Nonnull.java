package me.syncwrld.booter.libs.javax.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import me.syncwrld.booter.libs.javax.annotation.meta.TypeQualifier;
import me.syncwrld.booter.libs.javax.annotation.meta.TypeQualifierValidator;
import me.syncwrld.booter.libs.javax.annotation.meta.When;

@Documented
@TypeQualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface Nonnull {
  When when() default When.ALWAYS;
  
  public static class Checker implements TypeQualifierValidator<Nonnull> {
    public When forConstantValue(Nonnull qualifierArgument, Object value) {
      if (value == null)
        return When.NEVER; 
      return When.ALWAYS;
    }
  }
}
