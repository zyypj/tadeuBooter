package me.syncwrld.booter.libs.checkerfw.framework.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(RetentionPolicy.SOURCE)
public @interface CFComment {
  String[] value();
}
