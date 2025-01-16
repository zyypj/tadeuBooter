package com.github.zyypj.tadeuBooter.minecraft.object;

import lombok.Generated;

/**
 * Uma classe que representa um mapa com quatro entradas.
 * @param <K> Tipo da chave principal.
 * @param <V1> Tipo do primeiro valor.
 * @param <V2> Tipo do segundo valor.
 * @param <V3> Tipo do terceiro valor.
 */
public class TripleMap<K, V1, V2, V3> {

    private K key;
    private V1 value1;
    private V2 value2;
    private V3 value3;

    /**
     * Construtor para inicializar a QuadMap com quatro elementos.
     *
     * @param key    Chave principal.
     * @param value1 Primeiro valor.
     * @param value2 Segundo valor.
     * @param value3 Terceiro valor.
     */
    public TripleMap(K key, V1 value1, V2 value2, V3 value3) {
        this.key = key;
        this.value1 = value1;
        this.value2 = value2;
        this.value3 = value3;
    }

    @Generated
    public K getKey() {
        return this.key;
    }

    @Generated
    public V1 getValue1() {
        return this.value1;
    }

    @Generated
    public V2 getValue2() {
        return this.value2;
    }

    @Generated
    public V3 getValue3() {
        return this.value3;
    }

    @Generated
    public void setKey(K key) {
        this.key = key;
    }

    @Generated
    public void setValue1(V1 value1) {
        this.value1 = value1;
    }

    @Generated
    public void setValue2(V2 value2) {
        this.value2 = value2;
    }

    @Generated
    public void setValue3(V3 value3) {
        this.value3 = value3;
    }

    @Generated
    @Override
    public String toString() {
        return "QuadMap{" +
                "key=" + key +
                ", value1=" + value1 +
                ", value2=" + value2 +
                ", value3=" + value3 +
                '}';
    }
}