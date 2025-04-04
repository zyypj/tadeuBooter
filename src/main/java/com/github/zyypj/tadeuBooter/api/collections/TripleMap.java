package com.github.zyypj.tadeuBooter.api.collections;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TripleMap<K, V1, V2> {
    private K key;
    private V1 value;
    private V2 value2;
}