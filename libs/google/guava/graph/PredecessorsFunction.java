package me.syncwrld.booter.libs.google.guava.graph;

import me.syncwrld.booter.libs.google.errorprone.annotations.DoNotMock;
import me.syncwrld.booter.libs.google.guava.annotations.Beta;

@DoNotMock("Implement with a lambda, or use GraphBuilder to build a Graph with the desired edges")
@ElementTypesAreNonnullByDefault
@Beta
public interface PredecessorsFunction<N> {
  Iterable<? extends N> predecessors(N paramN);
}
