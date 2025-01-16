package me.syncwrld.booter.libs.checkerfw.checker.index.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.syncwrld.booter.libs.checkerfw.framework.qual.JavaExpression;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface HasSubsequence {
  @JavaExpression
  String subsequence();
  
  @JavaExpression
  String from();
  
  @JavaExpression
  String to();
}
