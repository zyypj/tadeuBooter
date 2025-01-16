package me.syncwrld.booter.libs.checkerfw.checker.regex.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.syncwrld.booter.libs.checkerfw.framework.qual.InvisibleQualifier;
import me.syncwrld.booter.libs.checkerfw.framework.qual.SubtypeOf;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({})
@InvisibleQualifier
@SubtypeOf({UnknownRegex.class})
public @interface PartialRegex {
  String value() default "";
}
