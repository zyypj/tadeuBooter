package com.github.zyypj.tadeuBooter.database;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.sql.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe abstrata que representa a base para operações com um banco de dados.
 * Oferece métodos para abrir, fechar, atualizar, consultar e salvar informações.
 *
 * @param <M> O modelo de banco de dados que esta classe gerencia.
 */
@Data
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Database<M extends DatabaseModel<?>> {

    private final M model;
    private final DatabaseHolder holder;
    protected final Properties properties;
    protected Connection connection;
    private final @NonNull Logger logger;

    /**
     * Construtor que inicializa o banco de dados com o modelo, holder e propriedades fornecidos.
     *
     * @param model      O modelo de banco de dados.
     * @param holder     O holder do banco de dados.
     * @param properties As propriedades da conexão.
     */
    public Database(@NonNull M model, @NonNull DatabaseHolder<?> holder, @NonNull Properties properties) {
        this(model, holder, properties, holder.getLogger());
    }

    /**
     * Verifica se a conexão com o banco de dados está fechada.
     *
     * @return true se a conexão estiver fechada, caso contrário false.
     */
    public boolean isClosed() {
        return connection == null;
    }

    /**
     * Abre uma conexão com o banco de dados e executa um callback opcional após a abertura.
     *
     * @param callback Callback opcional a ser executado após abrir a conexão.
     */
    public void open(Consumer<Database<M>> callback) {
        try {
            if (!isClosed()) return;
            connection = DriverManager.getConnection(getJdbcUrl(), properties);
            if (callback != null)
                callback.accept(this);
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Error while opening the database connection", e);
        }
    }

    /**
     * Abre uma conexão com o banco de dados sem callback.
     */
    public void open() {
        open(null);
    }

    /**
     * Fecha a conexão com o banco de dados.
     */
    public void close() {
        close(null);
    }

    /**
     * Fecha a conexão com o banco de dados e executa um callback opcional após o fechamento.
     *
     * @param callback Callback opcional a ser executado após fechar a conexão.
     */
    public void close(Consumer<Database<M>> callback) {
        try {
            if (isClosed()) return;
            connection.close();
            connection = null;
            if (callback != null)
                callback.accept(this);
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Error while closing the database connection", e);
        }
    }

    /**
     * Executa uma atualização no banco de dados usando o comando fornecido.
     *
     * @param command      O comando SQL a ser executado.
     * @param replacements Os parâmetros para o comando.
     * @return A instância atual do banco de dados.
     */
    public Database<M> update(String command, Object... replacements) {
        try {
            returnUpdate(command, replacements).close();
        } catch (SQLException e) {
            getLogger().log(Level.SEVERE, "Error while executing \"" + command + "\" with the arguments " + Arrays.toString(replacements), e);
        }
        return this;
    }

    /**
     * Prepara e executa um comando de atualização no banco de dados.
     *
     * @param command      O comando SQL a ser executado.
     * @param replacements Os parâmetros para o comando.
     * @return Um {@link PreparedStatement} preparado com o comando executado.
     */
    public PreparedStatement returnUpdate(String command, Object... replacements) {
        if (isClosed()) return null;
        try {
            PreparedStatement statement = connection.prepareStatement(command, Statement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < replacements.length; i++)
                statement.setObject(i + 1, replacements[i]);
            statement.executeUpdate();
            return statement;
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Error while executing \"" + command + "\" with the arguments " + Arrays.toString(replacements), e);
        }
        return null;
    }

    /**
     * Executa uma consulta SQL no banco de dados.
     *
     * @param query        O comando SQL a ser executado.
     * @param replacements Os parâmetros para o comando.
     * @return Um {@link ResultSet} contendo os resultados da consulta.
     */
    public ResultSet query(String query, Object... replacements) {
        if (isClosed()) return null;
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            for (int i = 0; i < replacements.length; i++)
                statement.setObject(i + 1, replacements[i]);
            return statement.executeQuery();
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Error while executing \"" + query + "\" with the arguments " + Arrays.toString(replacements), e);
            return null;
        }
    }

    /**
     * Obtém a URL JDBC específica do banco de dados.
     *
     * @return A URL JDBC.
     */
    public abstract String getJdbcUrl();

    /**
     * Salva um objeto no banco de dados, determinando se ele deve ser inserido, atualizado ou excluído.
     *
     * @param object O objeto a ser salvo.
     * @param table  A tabela onde o objeto será salvo.
     */
    public void save(DatabaseStorable object, String table) {
        if (object == null)
            return;

        if (object.isDeleted()) {
            delete(object, table);
            return;
        }

        if (object.isInDatabase()) {
            if (object.isDirty())
                update(object, table);
            return;
        }

        insert(object, table);
    }

    /**
     * Atualiza um objeto no banco de dados.
     *
     * @param object O objeto a ser atualizado.
     * @param table  A tabela onde o objeto será atualizado.
     */
    protected void update(DatabaseStorable object, String table) {
        if (object == null)
            return;

        StringBuilder builder = new StringBuilder("UPDATE " + table + " SET ");
        Map<String, Object> where = new HashMap<>();
        Map<String, Object> data = object.updateToDatabase(this, table, where);

        if (data.isEmpty())
            return;

        for (Map.Entry<String, Object> entry : data.entrySet())
            builder.append(entry.getKey()).append(" = ?, ");
        builder.delete(builder.length() - 2, builder.length());

        if (!where.isEmpty()) {
            builder.append(" WHERE ");
            for (Map.Entry<String, Object> entry : where.entrySet())
                builder.append(entry.getKey()).append(" = ? AND ");
            builder.delete(builder.length() - 5, builder.length());
        }

        builder.append(";");

        object.setDirty(false);

        List<Object> list = new ArrayList<>(data.values());
        list.addAll(where.values());

        update(builder.toString(), list.toArray());
    }

    /**
     * Exclui um objeto do banco de dados.
     *
     * @param object O objeto a ser excluído.
     * @param table  A tabela onde o objeto será excluído.
     */
    protected void delete(DatabaseStorable object, String table) {
        if (object == null)
            return;

        if (!object.isInDatabase())
            return;

        Map<String, Object> where = object.deleteFromDatabase(this, table);
        if (where.isEmpty())
            return;

        StringBuilder builder = new StringBuilder("DELETE FROM " + table + " WHERE ");
        for (Map.Entry<String, Object> entry : where.entrySet())
            builder.append(entry.getKey()).append(" = ? AND ");
        builder.delete(builder.length() - 5, builder.length());

        builder.append(";");

        update(builder.toString(), where.values().toArray());
    }

    /**
     * Insere um objeto no banco de dados.
     *
     * @param object O objeto a ser inserido.
     * @param table  A tabela onde o objeto será inserido.
     */
    protected void insert(DatabaseStorable object, String table) {
        if (object == null)
            return;

        Map<String, Object> data = object.saveToDatabase(this, table);
        if (data.isEmpty())
            return;

        StringBuilder builder = new StringBuilder("INSERT INTO ").append(table).append(" (");
        for (String s : data.keySet()) {
            builder.append(s).append(", ");
        }
        builder.delete(builder.length() - 2, builder.length());

        builder.append(") VALUES (");
        for (int i = 0; i < data.size(); i++) {
            builder.append("?, ");
        }
        builder.delete(builder.length() - 2, builder.length());

        builder.append(");");

        object.setInDatabase(true);
        object.setDirty(false);

        update(builder.toString(), data.values().toArray());
    }

    /**
     * Salva múltiplos objetos no banco de dados em uma tabela específica.
     *
     * @param table   A tabela onde os objetos serão salvos.
     * @param objects Os objetos a serem salvos.
     */
    public void save(@NonNull String table, @NonNull DatabaseStorable... objects) {
        for (DatabaseStorable object : objects)
            save(object, table);
    }

    /**
     * Salva múltiplos objetos no banco de dados usando um {@link Iterable}.
     *
     * @param objects Os objetos a serem salvos.
     * @param table   A tabela onde os objetos serão salvos.
     */
    public void save(@NonNull Iterable<? extends DatabaseStorable> objects, @NonNull String table) {
        for (DatabaseStorable object : objects)
            save(object, table);
    }
}