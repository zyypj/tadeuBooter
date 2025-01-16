package me.syncwrld.booter.libs.google.guava.util.concurrent;

import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.collect.ImmutableMultimap;

@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
interface ServiceManagerBridge {
  ImmutableMultimap<Service.State, Service> servicesByState();
}
