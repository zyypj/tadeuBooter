package com.github.zyypj.tadeuBooter.minecraft.objects;

import lombok.Generated;

/**
 * Uma classe que representa um mapa com quatro entradas.
 *
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
     * Construtor para inicializar o TripleMap com quatro elementos.
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

    /**
     * Obtém a chave principal do mapa.
     *
     * @return A chave principal.
     */
    @Generated
    public K getKey() {
        return this.key;
    }

    /**
     * Obtém o primeiro valor associado à chave.
     *
     * @return O primeiro valor.
     */
    @Generated
    public V1 getValue1() {
        return this.value1;
    }

    /**
     * Obtém o segundo valor associado à chave.
     *
     * @return O segundo valor.
     */
    @Generated
    public V2 getValue2() {
        return this.value2;
    }

    /**
     * Obtém o terceiro valor associado à chave.
     *
     * @return O terceiro valor.
     */
    @Generated
    public V3 getValue3() {
        return this.value3;
    }

    /**
     * Define a chave principal do mapa.
     *
     * @param key Nova chave principal.
     */
    @Generated
    public void setKey(K key) {
        this.key = key;
    }

    /**
     * Define o primeiro valor associado à chave.
     *
     * @param value1 Novo primeiro valor.
     */
    @Generated
    public void setValue1(V1 value1) {
        this.value1 = value1;
    }

    /**
     * Define o segundo valor associado à chave.
     *
     * @param value2 Novo segundo valor.
     */
    @Generated
    public void setValue2(V2 value2) {
        this.value2 = value2;
    }

    /**
     * Define o terceiro valor associado à chave.
     *
     * @param value3 Novo terceiro valor.
     */
    @Generated
    public void setValue3(V3 value3) {
        this.value3 = value3;
    }

    /**
     * Retorna uma representação em String do TripleMap.
     *
     * @return Uma string contendo a chave e os três valores.
     */
    @Generated
    @Override
    public String toString() {
        return "TripleMap{" +
                "key=" + key +
                ", value1=" + value1 +
                ", value2=" + value2 +
                ", value3=" + value3 +
                '}';
    }
}