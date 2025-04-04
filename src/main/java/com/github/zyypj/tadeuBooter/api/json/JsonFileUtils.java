package com.github.zyypj.tadeuBooter.api.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.github.zyypj.tadeuBooter.loader.Constants;

import java.io.*;
import java.util.Map;

public class JsonFileUtils {

    public String prettyPrint(JsonObject obj) {
        return Constants.GSON.toJson(obj);
    }

    public JsonObject mergeObjects(JsonObject base, JsonObject override) {
        for (Map.Entry<String, JsonElement> entry : override.entrySet()) {
            base.add(entry.getKey(), entry.getValue());
        }
        return base;
    }

    public JsonObject toObject(String json) {
        return Constants.GSON.fromJson(json, JsonObject.class);
    }

    public JsonObject getOrCreateObject(File file) throws IOException {
        if (!file.exists()) {
            saveObject(file, new JsonObject());
        }
        return getObject(file);
    }

    public JsonObject getObject(File file) throws IOException {
        if (!file.exists()) {
            System.out.println("Arquivo não encontrado: " + file.getAbsolutePath());
        }

        try (Reader reader = new FileReader(file);
             JsonReader jsonReader = new JsonReader(reader)) {
            return Constants.GSON.fromJson(jsonReader, JsonObject.class);
        }
    }

    public JsonObject getObject(InputStream inputStream) throws IOException {
        try (Reader reader = new BufferedReader(new InputStreamReader(inputStream));
             JsonReader jsonReader = new JsonReader(reader)) {
            return Constants.GSON.fromJson(jsonReader, JsonObject.class);
        }
    }

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

    public JsonArray getArray(InputStream inputStream) {
        try (Reader reader = new BufferedReader(new InputStreamReader(inputStream));
             JsonReader jsonReader = new JsonReader(reader)) {
            return Constants.GSON.fromJson(jsonReader, JsonArray.class);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao processar o InputStream JSON.", e);
        }
    }
    public void saveObject(File file, JsonObject object) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            Constants.GSON.toJson(object, writer);
        }
    }

    public void saveArray(File file, JsonArray array) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            Constants.GSON.toJson(array, writer);
        }
    }
}