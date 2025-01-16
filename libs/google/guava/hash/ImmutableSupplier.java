package me.syncwrld.booter.libs.google.guava.hash;

import me.syncwrld.booter.libs.google.errorprone.annotations.Immutable;
import me.syncwrld.booter.libs.google.guava.base.Supplier;

@Immutable
@ElementTypesAreNonnullByDefault
interface ImmutableSupplier<T> extends Supplier<T> {}
