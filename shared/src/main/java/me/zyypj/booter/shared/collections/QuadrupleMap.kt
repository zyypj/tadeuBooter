package me.zyypj.booter.shared.collections

data class QuadrupleMap<K, V1, V2, V3>(
    var key: K,
    var value: V1,
    var value2: V2,
    var value3: V3
)