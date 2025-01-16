package me.syncwrld.booter.libs.google.guava.graph;

import me.syncwrld.booter.libs.google.errorprone.annotations.DoNotMock;
import me.syncwrld.booter.libs.google.guava.annotations.Beta;

@DoNotMock("Implement with a lambda, or use GraphBuilder to build a Graph with the desired edges")
@ElementTypesAreNonnullByDefault
@Beta
public interface SuccessorsFunction<N> {
  Iterable<? extends N> successors(N paramN);
}
