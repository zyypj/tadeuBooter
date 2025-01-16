package com.github.zyypj.tadeuBooter.database.controler;

import com.github.zyypj.tadeuBooter.database.controler.listener.ControllerListener;
import com.github.zyypj.tadeuBooter.database.dao.DAO;
import com.github.zyypj.tadeuBooter.minecraft.object.Cache;
import lombok.Data;
import lombok.NonNull;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe abstrata que gerencia a interação entre o cache e o banco de dados.
 * Oferece métodos para busca, armazenamento em cache, salvamento e gerenciamento de listeners.
 *
 * @param <K> O tipo da chave usada para identificar valores no cache e no banco de dados.
 * @param <V> O tipo dos valores gerenciados pelo cache e DAO.
 * @param <C> O tipo do cache usado pelo controlador.
 * @param <D> O tipo do DAO usado pelo controlador.
 */
@Data
public abstract class Controller<K, V, C extends Cache<K, V, ?>, D extends DAO<K, V>> {

    @NonNull
    private final C cache;
    @NonNull
    private final D dao;
    private final @NonNull Logger logger;
    private final @NonNull Set<ControllerListener> listeners = new HashSet<>();

    /**
     * Construtor que inicializa o controlador com o cache e DAO fornecidos.
     *
     * @param cache O cache para gerenciamento de dados em memória.
     * @param dao   O DAO para operações no banco de dados.
     */
    public Controller(@NonNull C cache, @NonNull D dao) {
        this.cache = cache;
        this.dao = dao;
        this.logger = Logger.getLogger(getClass().getSimpleName());
    }

    /**
     * Construtor que inicializa o controlador com o cache, DAO e logger fornecidos.
     *
     * @param cache  O cache para gerenciamento de dados em memória.
     * @param dao    O DAO para operações no banco de dados.
     * @param logger O logger para registrar eventos e erros.
     */
    public Controller(@NonNull C cache, @NonNull D dao, @NonNull Logger logger) {
        this.cache = cache;
        this.dao = dao;
        this.logger = logger;
    }

    /**
     * Busca um valor no cache e, se não estiver presente, busca no banco de dados.
     *
     * @param key A chave usada para identificar o valor.
     * @return Um {@link CompletableFuture} que será completado com o valor buscado.
     */
    public CompletableFuture<V> get(@NonNull K key) {
        return cache.get(key)
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> dao.find(key).whenComplete((value, throwable) -> {
                    if (throwable != null) {
                        onGetError(key, throwable);
                        return;
                    }

                    if (value == null)
                        return;

                    cache(value);
                }));
    }

    /**
     * Método chamado quando ocorre um erro ao buscar um valor.
     *
     * @param key       A chave usada para identificar o valor.
     * @param throwable A exceção lançada durante a operação.
     */
    protected void onGetError(@NonNull K key, @NonNull Throwable throwable) {
        getLogger().log(Level.SEVERE, "Error while getting the value with the key " + key, throwable);
    }

    /**
     * Armazena o valor no cache.
     *
     * @param value O valor a ser armazenado.
     */
    public abstract void cache(@NonNull V value);

    /**
     * Armazena múltiplos valores no cache.
     *
     * @param values Os valores a serem armazenados.
     */
    public void cacheAll(@NonNull Iterable<V> values) {
        for (V value : values)
            cache(value);
    }

    /**
     * Método chamado quando o controlador é desligado.
     * A implementação padrão chama o método {@link #save()} e espera a conclusão do {@link CompletableFuture}.
     *
     * @apiNote Este método não é chamado automaticamente, deve ser invocado manualmente.
     */
    public void shutdown() {
        save().join();
    }

    /**
     * Método chamado para salvar todos os dados no banco de dados.
     * Implementações específicas podem chamar este método periodicamente.
     *
     * @return Um {@link CompletableFuture} indicando a conclusão do salvamento.
     */
    public CompletableFuture<Void> save() {
        return CompletableFuture.allOf(cache.getAll().stream().map(dao::update).toArray(CompletableFuture[]::new));
    }

    @Override
    public int hashCode() {
        return Controller.class.hashCode();
    }

    /**
     * Adiciona um listener para monitorar eventos do controlador.
     *
     * @param listener O listener a ser adicionado.
     */
    public void addListener(@NonNull ControllerListener listener) {
        listeners.add(listener);
    }

    /**
     * Remove um listener do controlador.
     *
     * @param listener O listener a ser removido.
     */
    public void removeListener(@NonNull ControllerListener listener) {
        listeners.remove(listener);
    }
}