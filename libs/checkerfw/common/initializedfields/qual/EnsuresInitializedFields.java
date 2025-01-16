package me.syncwrld.booter.libs.checkerfw.common.initializedfields.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.syncwrld.booter.libs.checkerfw.framework.qual.InheritedAnnotation;
import me.syncwrld.booter.libs.checkerfw.framework.qual.PostconditionAnnotation;
import me.syncwrld.booter.libs.checkerfw.framework.qual.QualifierArgument;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@PostconditionAnnotation(qualifier = InitializedFields.class)
@InheritedAnnotation
@Repeatable(EnsuresInitializedFields.List.class)
public @interface EnsuresInitializedFields {
  String[] value() default {"this"};
  
  @QualifierArgument("value")
  String[] fields();
  
  @Documented
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
  @PostconditionAnnotation(qualifier = InitializedFields.class)
  @InheritedAnnotation
  public static @interface List {
    EnsuresInitializedFields[] value();
  }
}
