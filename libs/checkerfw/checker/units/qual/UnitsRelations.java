package me.syncwrld.booter.libs.checkerfw.checker.units.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface UnitsRelations {
  Class<?> value();
}
