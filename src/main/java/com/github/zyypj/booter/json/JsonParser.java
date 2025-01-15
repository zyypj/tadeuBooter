package com.github.zyypj.booter.json;

import com.github.zyypj.booter.Constants;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import java.io.*;

/**
 * Classe utilitária para manipulação de arquivos JSON.
 * Permite leitura e escrita de objetos JSON em arquivos ou streams de entrada.
 */
public class JsonParser {

    /**
     * Lê um arquivo JSON e retorna seu conteúdo como um JsonObject.
     *
     * @param file O arquivo JSON a ser lido.
     * @return Um JsonObject representando o conteúdo do arquivo.
     * @throws IOException Caso ocorra um erro na leitura do arquivo.
     */
    public JsonObject getObject(File file) throws IOException {
        if (!file.exists()) {
            System.out.println("Arquivo não encontrado: " + file.getAbsolutePath());
        }

        try (Reader reader = new FileReader(file);
             JsonReader jsonReader = new JsonReader(reader)) {
            return Constants.GSON.fromJson(jsonReader, JsonObject.class);
        }
    }

    /**
     * Lê um InputStream JSON e retorna seu conteúdo como um JsonObject.
     *
     * @param inputStream O InputStream a ser lido.
     * @return Um JsonObject representando o conteúdo do InputStream.
     * @throws IOException Caso ocorra um erro na leitura do stream.
     */
    public JsonObject getObject(InputStream inputStream) throws IOException {
        try (Reader reader = new BufferedReader(new InputStreamReader(inputStream));
             JsonReader jsonReader = new JsonReader(reader)) {
            return Constants.GSON.fromJson(jsonReader, JsonObject.class);
        }
    }

    /**
     * Lê um arquivo JSON e retorna seu conteúdo como um JsonArray.
     *
     * @param file O arquivo JSON a ser lido.
     * @return Um JsonArray representando o conteúdo do arquivo.
     */
    public JsonArray getArray(File file) {
        if (!file.exists()) {
            System.out.println("Arquivo não encontrado: " + file.getAbsolutePath());
        }

        try (Reader reader = new FileReader(file);
             JsonReader jsonReader = new JsonReader(reader)) {
            return Constants.GSON.fromJson(jsonReader, JsonArray.class);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler o arquivo JSON: " + file.getAbsolutePath(), e);
        }
    }

    /**
     * Lê um InputStream JSON e retorna seu conteúdo como um JsonArray.
     *
     * @param inputStream O InputStream a ser lido.
     * @return Um JsonArray representando o conteúdo do InputStream.
     */
    public JsonArray getArray(InputStream inputStream) {
        try (Reader reader = new BufferedReader(new InputStreamReader(inputStream));
             JsonReader jsonReader = new JsonReader(reader)) {
            return Constants.GSON.fromJson(jsonReader, JsonArray.class);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao processar o InputStream JSON.", e);
        }
    }

    /**
     * Salva um JsonObject em um arquivo.
     *
     * @param file   O arquivo onde o JsonObject será salvo.
     * @param object O JsonObject a ser salvo.
     * @throws IOException Caso ocorra um erro na escrita do arquivo.
     */
    public void saveObject(File file, JsonObject object) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            Constants.GSON.toJson(object, writer);
        }
    }

    /**
     * Salva um JsonArray em um arquivo.
     *
     * @param file  O arquivo onde o JsonArray será salvo.
     * @param array O JsonArray a ser salvo.
     * @throws IOException Caso ocorra um erro na escrita do arquivo.
     */
    public void saveArray(File file, JsonArray array) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            Constants.GSON.toJson(array, writer);
        }
    }
}