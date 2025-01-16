package me.syncwrld.booter.libs.javax.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import me.syncwrld.booter.libs.javax.annotation.meta.TypeQualifier;
import me.syncwrld.booter.libs.javax.annotation.meta.When;

@Documented
@TypeQualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface Untainted {
  When when() default When.ALWAYS;
}
