package com.github.zyypj.booter.minecraft.maps;

import lombok.Generated;

public class TripleMap<K, V, V2> {

    private K key;
    private V value;
    private V2 value2;

    public TripleMap(K key, V value, V2 value2) {
        this.key = key;
        this.value = value;
        this.value2 = value2;
    }

    @Generated
    public K getKey() {
        return this.key;
    }

    @Generated
    public V getValue() {
        return this.value;
    }

    @Generated
    public V2 getValue2() {
        return this.value2;
    }

    @Generated
    public void setKey(K key) {
        this.key = key;
    }

    @Generated
    public void setValue(V value) {
        this.value = value;
    }

    @Generated
    public void setValue2(V2 value2) {
        this.value2 = value2;
    }

    @Generated
    public String toString() {
        return "TripleMap{" +
                "key=" + key +
                ", value=" + value +
                ", value2=" + value2 +
                '}';
    }
}