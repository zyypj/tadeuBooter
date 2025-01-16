package com.github.zyypj.tadeuBooter.minecraft.object;

import lombok.Generated;

/**
 * Classe que representa um mapa de pares contendo uma chave (K) e dois valores (V e V2).
 *
 * @param <K> Tipo da chave.
 * @param <V> Tipo do primeiro valor.
 * @param <V2> Tipo do segundo valor.
 */
public class PairMap<K, V, V2> {

    private K key; // Chave do par
    private V value; // Primeiro valor do par
    private V2 value2; // Segundo valor do par

    /**
     * Construtor para inicializar o PairMap com chave e valores fornecidos.
     *
     * @param key Chave do par.
     * @param value Primeiro valor do par.
     * @param value2 Segundo valor do par.
     */
    public PairMap(K key, V value, V2 value2) {
        this.key = key;
        this.value = value;
        this.value2 = value2;
    }

    /**
     * Obtém a chave deste par.
     *
     * @return A chave.
     */
    @Generated
    public K getKey() {
        return this.key;
    }

    /**
     * Obtém o primeiro valor deste par.
     *
     * @return O primeiro valor.
     */
    @Generated
    public V getValue() {
        return this.value;
    }

    /**
     * Obtém o segundo valor deste par.
     *
     * @return O segundo valor.
     */
    @Generated
    public V2 getValue2() {
        return this.value2;
    }

    /**
     * Define uma nova chave para este par.
     *
     * @param key A nova chave.
     */
    @Generated
    public void setKey(K key) {
        this.key = key;
    }

    /**
     * Define um novo primeiro valor para este par.
     *
     * @param value O novo primeiro valor.
     */
    @Generated
    public void setValue(V value) {
        this.value = value;
    }

    /**
     * Define um novo segundo valor para este par.
     *
     * @param value2 O novo segundo valor.
     */
    @Generated
    public void setValue2(V2 value2) {
        this.value2 = value2;
    }

    /**
     * Retorna uma representação em string deste PairMap.
     *
     * @return Uma string representando o PairMap com chave e valores.
     */
    @Generated
    public String toString() {
        return "PairMap{" +
                "key=" + key +
                ", value=" + value +
                ", value2=" + value2 +
                '}';
    }
}