package com.github.zyypj.tadeuBooter.api.downloaders.dependencies;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.github.zyypj.tadeuBooter.loader.Constants;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;

/**
 * Essa classe foi inspirada numa implementação já existente!
 *
 * @author syncwrld (github.com/syncwrld)
 */
@Getter
@RequiredArgsConstructor
public class Dependencies {
    private final Queue<CompletableFuture<Void>> downloadQueue = new ConcurrentLinkedQueue<>();
    private final Plugin plugin;
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    /**
     * Cria uma instância de Dependencies associada ao plugin.
     *
     * @param plugin Plugin associado
     * @return Instância de Dependencies
     */
    public static Dependencies of(Plugin plugin) {
        return new Dependencies(plugin);
    }

    /**
     * Lê um arquivo JSON e converte-o em uma lista de dependências.
     *
     * @param file Arquivo JSON contendo as dependências
     * @return Array de dependências carregadas
     */
    public static Dependency[] of(File file) {
        List<Dependency> dependencies = new ArrayList<>();

        try (Reader reader = new FileReader(file);
             JsonReader jsonReader = new JsonReader(reader)) {

            JsonObject jsonObject = Constants.GSON.fromJson(jsonReader, JsonObject.class);

            for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                JsonArray dependencyArray = entry.getValue().getAsJsonArray();
                if (dependencyArray.size() >= 3) {
                    DependencyInfo dependencyInfo = new DependencyInfo(
                            dependencyArray.get(0).getAsString(),
                            dependencyArray.get(1).getAsString(),
                            dependencyArray.get(2).getAsString()
                    );
                    dependencies.add(new Dependency(dependencyInfo));
                } else {
                    Bukkit.getConsoleSender().sendMessage("§4Dependência inválida no JSON: " + entry.getKey());
                }
            }
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage("§4Erro ao ler o arquivo de dependências: " + file.getAbsolutePath());
            Bukkit.getConsoleSender().sendMessage(e.getMessage());
        }

        return dependencies.toArray(new Dependency[0]);
    }

    /**
     * Adiciona uma dependência à fila de downloads.
     *
     * @param dependency Dependência a ser baixada
     */
    public void queue(Dependency dependency) {
        this.downloadQueue.add(dependency.download());
    }

    /**
     * Adiciona múltiplas dependências à fila de downloads.
     *
     * @param dependencies Dependências a serem baixadas
     */
    public void queue(Dependency... dependencies) {
        Arrays.stream(dependencies).forEach(this::queue);
    }

    /**
     * Remove uma dependência da fila de downloads.
     *
     * @param dependency Dependência a ser removida
     */
    public void remove(Dependency dependency) {
        this.downloadQueue.removeIf(future -> future.toString().equals(dependency.download().toString()));
    }

    /**
     * Executa a fila de downloads de forma assíncrona.
     */
    public void runQueue() {
        if (downloadQueue.isEmpty()) {
            Bukkit.getConsoleSender().sendMessage("§2Nenhuma dependência para baixar.");
            return;
        }

        CompletableFuture<Void> allDownloads = CompletableFuture.allOf(
                downloadQueue.toArray(new CompletableFuture[0])
        );

        allDownloads.join();
    }
}