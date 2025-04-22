package com.github.zyypj.tadeuBooter.api.collections

data class TripleMap<K, V1, V2>(
    var key: K,
    var value: V1,
    var value2: V2
)