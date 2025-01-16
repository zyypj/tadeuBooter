package me.syncwrld.booter.libs.google.errorprone.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.syncwrld.booter.libs.javax.lang.model.element.Modifier;

@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.ANNOTATION_TYPE})
public @interface RequiredModifiers {
  @Deprecated
  Modifier[] value() default {};
  
  Modifier[] modifier() default {};
}
