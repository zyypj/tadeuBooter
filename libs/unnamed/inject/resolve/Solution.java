package me.syncwrld.booter.libs.unnamed.inject.resolve;

import java.util.List;
import me.syncwrld.booter.libs.unnamed.inject.resolve.solution.InjectableField;
import me.syncwrld.booter.libs.unnamed.inject.resolve.solution.InjectableMethod;

class Solution {
  Object constructor = ConstructorResolver.CONSTRUCTOR_NOT_DEFINED;
  
  List<InjectableField> fields;
  
  List<InjectableMethod> methods;
}
