package com.github.zyypj.tadeuBooter.minecraft.objects;

import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

/**
 * Classe abstrata que representa um cache genérico com chave (K), valor (V) e um tipo intermediário (T).
 * Permite operações básicas de adição, remoção, consulta e manipulação de dados no cache.
 *
 * @param <K> Tipo da chave usada para identificar os valores no cache.
 * @param <V> Tipo do valor armazenado no cache.
 * @param <T> Tipo intermediário utilizado para criar entradas no cache.
 */
public abstract class Cache<K, V, T> {

    /**
     * Mapa que armazena os dados do cache, onde a chave (K) está associada ao valor (V).
     */
    protected final Map<K, V> cache;

    /**
     * Construtor que inicializa o cache com o mapa retornado por {@link #getInitialMap()}.
     */
    protected Cache() {
        cache = getInitialMap();
    }

    /**
     * Adiciona um elemento ao cache com base no tipo intermediário (T).
     *
     * @param t Elemento do tipo T a ser adicionado ao cache.
     */
    public void add(@NotNull T t) {
        final Pair<K, V> apply = apply(t);
        cache.put(apply.getFirst(), apply.getSecond());
    }

    /**
     * Adiciona uma coleção de elementos do tipo T ao cache.
     *
     * @param collection Coleção de elementos a serem adicionados.
     */
    public void addAll(@NotNull Collection<T> collection) {
        for (T t : collection)
            add(t);
    }

    /**
     * Adiciona todos os elementos de um mapa ao cache.
     *
     * @param map Mapa contendo as entradas a serem adicionadas ao cache.
     */
    public void addAll(@NotNull Map<K, V> map) {
        cache.putAll(map);
    }

    /**
     * Remove um elemento do cache com base em sua chave.
     *
     * @param key Chave do elemento a ser removido.
     * @return Um {@link Optional} contendo o valor removido ou vazio se a chave não existir.
     */
    public Optional<V> remove(@Nullable K key) {
        if (key == null)
            return Optional.empty();
        return Optional.ofNullable(cache.remove(key));
    }

    /**
     * Verifica se uma chave existe no cache.
     *
     * @param key Chave a ser verificada.
     * @return true se a chave existir no cache, caso contrário false.
     */
    public boolean has(@Nullable K key) {
        if (key == null)
            return false;
        return cache.containsKey(key);
    }

    /**
     * Obtém o valor associado a uma chave, se existir.
     *
     * @param key Chave do valor a ser recuperado.
     * @return Um {@link Optional} contendo o valor, ou vazio se a chave não existir.
     */
    @NotNull
    public Optional<V> get(@Nullable K key) {
        if (key == null)
            return Optional.empty();
        return Optional.ofNullable(cache.get(key));
    }

    /**
     * Obtém o valor associado a uma chave ou insere um novo valor gerado a partir de um tipo T.
     *
     * @param key Chave do valor.
     * @param def Valor do tipo T a ser inserido caso a chave não exista.
     * @return Valor associado à chave.
     */
    @NotNull
    public V getOrInsert(@NotNull K key, @NonNull T def) {
        if (cache.containsKey(key))
            return cache.get(key);

        Pair<K, V> pair = apply(def);
        add(def);
        return pair.getSecond();
    }

    /**
     * Obtém o valor associado a uma chave ou insere um novo valor gerado por um {@link Supplier}.
     *
     * @param key Chave do valor.
     * @param defaultValue Supplier que fornece o valor a ser inserido caso a chave não exista.
     * @return Valor associado à chave.
     */
    @NotNull
    public V getOrInsert(@NotNull K key, @NotNull Supplier<T> defaultValue) {
        if (cache.containsKey(key))
            return cache.get(key);

        T def = defaultValue.get();
        if (def == null)
            throw new NullPointerException("The supplier returned a null value");

        Pair<K, V> pair = apply(def);
        add(def);
        return pair.getSecond();
    }

    /**
     * Retorna o conjunto de chaves armazenadas no cache.
     *
     * @return Conjunto de chaves do cache.
     */
    @NotNull
    public Set<K> keySet() {
        return cache.keySet();
    }

    /**
     * Retorna o mapa interno que representa o cache.
     *
     * @return Mapa interno do cache.
     */
    @NotNull
    public Map<K, V> getMap() {
        return cache;
    }

    /**
     * Retorna todos os valores armazenados no cache.
     *
     * @return Coleção de valores do cache.
     */
    @NotNull
    public Collection<V> getAll() {
        return cache.values();
    }

    /**
     * Limpa todos os elementos do cache.
     */
    public void clear() {
        cache.clear();
    }

    /**
     * Retorna o número de elementos armazenados no cache.
     *
     * @return Tamanho do cache.
     */
    public int size() {
        return cache.size();
    }

    /**
     * Verifica se o cache está vazio.
     *
     * @return true se o cache estiver vazio, caso contrário false.
     */
    public boolean isEmpty() {
        return cache.isEmpty();
    }

    /**
     * Método abstrato para converter um objeto do tipo T em um par (chave, valor).
     *
     * @param t Objeto do tipo T a ser convertido.
     * @return Par contendo a chave e o valor gerados a partir do objeto T.
     */
    @NotNull
    public abstract Pair<K, V> apply(T t);

    /**
     * Método para obter o mapa inicial utilizado pelo cache.
     * Por padrão, retorna um {@link HashMap}.
     *
     * @return Mapa inicial do cache.
     */
    @NotNull
    protected Map<K, V> getInitialMap() {
        return new HashMap<>();
    }

    /**
     * Obtém os valores associados a uma lista de chaves fornecida.
     *
     * @param keys Iterable contendo as chaves.
     * @return Lista de valores correspondentes às chaves fornecidas.
     */
    @NotNull
    public List<V> get(@NonNull Iterable<K> keys) {
        List<V> result = new ArrayList<>(size());
        for (K key : keys)
            get(key).ifPresent(result::add);
        return result;
    }
}