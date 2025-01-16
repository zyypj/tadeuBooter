package me.syncwrld.booter.libs.unnamed.inject.resolve.solution;

import java.lang.reflect.Member;
import me.syncwrld.booter.libs.unnamed.inject.impl.InjectorImpl;
import me.syncwrld.booter.libs.unnamed.inject.impl.ProvisionStack;
import me.syncwrld.booter.libs.unnamed.inject.key.TypeReference;

public interface InjectableMember {
  TypeReference<?> getDeclaringType();
  
  Member getMember();
  
  Object inject(InjectorImpl paramInjectorImpl, ProvisionStack paramProvisionStack, Object paramObject);
}
