package me.syncwrld.booter.libs.javax.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import me.syncwrld.booter.libs.javax.annotation.meta.TypeQualifier;
import me.syncwrld.booter.libs.javax.annotation.meta.When;

@Documented
@TypeQualifier(applicableTo = CharSequence.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface Syntax {
  String value();
  
  When when() default When.ALWAYS;
}
