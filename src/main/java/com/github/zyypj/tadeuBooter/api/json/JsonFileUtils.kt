package com.github.zyypj.tadeuBooter.api.json

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.stream.JsonReader
import com.github.zyypj.tadeuBooter.loader.Constants
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import kotlin.jvm.JvmStatic
import kotlin.jvm.Throws

/**
 * Utilitários para leitura, gravação e manipulação de arquivos JSON usando Gson.
 * Métodos anotados com @JvmStatic são acessíveis diretamente de Java.
 */
object JsonFileUtils {

    /** Serializa o JsonObject em formato 'bonitão'. */
    @JvmStatic
    fun prettyPrint(obj: JsonObject): String = Constants.GSON.toJson(obj)

    /** Mescla os pares de "override" em "base" (substituindo chaves existentes). */
    @JvmStatic
    fun mergeObjects(base: JsonObject, override: JsonObject): JsonObject {
        for ((key, value) in override.entrySet()) {
            base.add(key, value)
        }
        return base
    }

    /** Converte uma string JSON em JsonObject. */
    @JvmStatic
    fun toObject(json: String): JsonObject =
        Constants.GSON.fromJson(json, JsonObject::class.java)

    /**
     * Lê ou cria um JsonObject no arquivo especificado.
     * @throws IOException se der erro de I/O
     */
    @JvmStatic
    @Throws(IOException::class)
    fun getOrCreateObject(file: File): JsonObject {
        if (!file.exists()) {
            saveObject(file, JsonObject())
        }
        return getObject(file)
    }

    /**
     * Lê um JsonObject de um arquivo.
     * @throws IOException se der erro de I/O
     */
    @JvmStatic
    @Throws(IOException::class)
    fun getObject(file: File): JsonObject {
        if (!file.exists()) {
            println("Arquivo não encontrado: ${file.absolutePath}")
        }
        FileReader(file).use { fr ->
            JsonReader(fr).use { jr ->
                return Constants.GSON.fromJson(jr, JsonObject::class.java)
            }
        }
    }

    /**
     * Lê um JsonObject de um InputStream.
     * @throws IOException se der erro de I/O
     */
    @JvmStatic
    @Throws(IOException::class)
    fun getObject(inputStream: InputStream): JsonObject {
        BufferedReader(InputStreamReader(inputStream)).use { br ->
            JsonReader(br).use { jr ->
                return Constants.GSON.fromJson(jr, JsonObject::class.java)
            }
        }
    }

    /**
     * Lê um JsonArray de um arquivo.
     * @throws RuntimeException se der erro de leitura
     */
    @JvmStatic
    fun getArray(file: File): JsonArray {
        if (!file.exists()) {
            println("Arquivo não encontrado: ${file.absolutePath}")
        }
        try {
            FileReader(file).use { fr ->
                JsonReader(fr).use { jr ->
                    return Constants.GSON.fromJson(jr, JsonArray::class.java)
                }
            }
        } catch (e: IOException) {
            throw RuntimeException("Erro ao ler o arquivo JSON: ${file.absolutePath}", e)
        }
    }

    /**
     * Lê um JsonArray de um InputStream.
     * @throws RuntimeException se der erro de leitura
     */
    @JvmStatic
    fun getArray(inputStream: InputStream): JsonArray {
        try {
            BufferedReader(InputStreamReader(inputStream)).use { br ->
                JsonReader(br).use { jr ->
                    return Constants.GSON.fromJson(jr, JsonArray::class.java)
                }
            }
        } catch (e: IOException) {
            throw RuntimeException("Erro ao processar o InputStream JSON.", e)
        }
    }

    /**
     * Grava um JsonObject em um arquivo.
     * @throws IOException se der erro de I/O
     */
    @JvmStatic
    @Throws(IOException::class)
    fun saveObject(file: File, obj: JsonObject) {
        BufferedWriter(FileWriter(file)).use { bw ->
            Constants.GSON.toJson(obj, bw)
        }
    }

    /**
     * Grava um JsonArray em um arquivo.
     * @throws IOException se der erro de I/O
     */
    @JvmStatic
    @Throws(IOException::class)
    fun saveArray(file: File, array: JsonArray) {
        BufferedWriter(FileWriter(file)).use { bw ->
            Constants.GSON.toJson(array, bw)
        }
    }
}