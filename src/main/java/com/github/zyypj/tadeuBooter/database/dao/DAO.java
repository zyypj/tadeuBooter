package com.github.zyypj.tadeuBooter.database.dao;

import com.github.zyypj.tadeuBooter.database.Database;
import lombok.Data;
import lombok.NonNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

/**
 * Classe abstrata que define um DAO (Data Access Object) para operações genéricas no banco de dados.
 * Gerencia operações assíncronas como busca, atualização e configuração inicial do banco de dados.
 *
 * @param <K> O tipo da chave usada para identificar os objetos.
 * @param <V> O tipo dos valores gerenciados pelo DAO.
 */
@Data
public abstract class DAO<K, V> {

    private final Database<?> database;
    private final ExecutorService executor;
    @NonNull
    private final Logger logger;

    /**
     * Construtor que inicializa o DAO com o banco de dados e executor fornecidos.
     *
     * @param database O banco de dados a ser usado.
     * @param executor O executor para operações assíncronas.
     */
    public DAO(Database<?> database, ExecutorService executor) {
        this.database = database;
        this.executor = executor;
        this.logger = Logger.getLogger(getClass().getName());
    }

    /**
     * Construtor protegido que inicializa o DAO com o banco de dados, executor e logger fornecidos.
     *
     * @param database O banco de dados a ser usado.
     * @param executor O executor para operações assíncronas.
     * @param logger   O logger para registrar eventos.
     */
    protected DAO(Database<?> database, ExecutorService executor, @NonNull Logger logger) {
        this.database = database;
        this.executor = executor;
        this.logger = logger;
    }

    /**
     * Busca um valor no banco de dados usando a chave fornecida.
     *
     * @param key A chave usada para identificar o valor.
     * @return Um {@link CompletableFuture} contendo o valor encontrado.
     */
    public abstract CompletableFuture<V> find(K key);

    /**
     * Atualiza um valor no banco de dados.
     *
     * @param value O valor a ser atualizado.
     * @return Um {@link CompletableFuture} indicando a conclusão da operação.
     */
    public abstract CompletableFuture<Void> update(V value);

    /**
     * Atualiza múltiplos valores no banco de dados a partir de um {@link Iterable}.
     *
     * @param values Os valores a serem atualizados.
     * @return Um {@link CompletableFuture} indicando a conclusão da operação para todos os valores.
     */
    public CompletableFuture<Void> updateAll(Iterable<V> values) {
        if (values instanceof Collection) {
            Collection<V> collection = (Collection<V>) values;
            return CompletableFuture.allOf(
                    collection.stream()
                            .map(this::update)
                            .toArray(CompletableFuture[]::new)
            );
        }
        throw new IllegalArgumentException("The given Iterable is not recognized by this method");
    }

    /**
     * Atualiza múltiplos valores no banco de dados a partir de um array.
     *
     * @param values Os valores a serem atualizados.
     * @return Um {@link CompletableFuture} indicando a conclusão da operação para todos os valores.
     */
    public CompletableFuture<Void> updateAll(V... values) {
        return CompletableFuture.allOf(Arrays.stream(values).map(this::update).toArray(CompletableFuture[]::new));
    }

    /**
     * Obtém todos os valores disponíveis no banco de dados.
     *
     * @return Um {@link CompletableFuture} contendo uma lista de todos os valores encontrados.
     */
    public abstract CompletableFuture<List<V>> fetchAll();

    /**
     * Configura o banco de dados para ser usado por este DAO.
     * Essa configuração pode incluir a criação de tabelas, índices e outros elementos necessários.
     */
    public abstract void setupDatabase();
}