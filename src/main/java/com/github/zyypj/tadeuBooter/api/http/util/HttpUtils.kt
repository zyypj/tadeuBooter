package com.github.zyypj.tadeuBooter.api.http.util

import com.google.gson.Gson
import java.io.*
import java.net.HttpURLConnection
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object HttpUtils {
    private val gson = Gson()

    @Throws(UnsupportedEncodingException::class)
    fun encodeUrl(raw: String): String =
        URLEncoder.encode(raw, StandardCharsets.UTF_8.name())

    @Throws(UnsupportedEncodingException::class)
    fun decodeUrl(encoded: String): String =
        URLDecoder.decode(encoded, StandardCharsets.UTF_8.name())

    @Throws(IOException::class)
    fun read(input: InputStream): String =
        input.bufferedReader().use { it.readText() }

    @Throws(IOException::class)
    fun <T> readJson(input: InputStream, clazz: Class<T>): T =
        gson.fromJson(read(input), clazz)

    fun parseHeaders(conn: HttpURLConnection): Map<String, String> =
        conn.headerFields.entries
            .filter { (k, v) -> k != null && v != null && v.isNotEmpty() }
            .associate { it.key!! to it.value.joinToString(", ") }

    fun getHeader(conn: HttpURLConnection, key: String): String? =
        conn.getHeaderField(key)

    fun getCookies(conn: HttpURLConnection): Map<String, String> =
        conn.headerFields["Set-Cookie"]
            ?.flatMap { it.split(";") }
            ?.mapNotNull {
                val parts = it.split("=", limit = 2)
                if (parts.size == 2) parts[0].trim() to parts[1].trim() else null
            }
            ?.toMap()
            ?: emptyMap()

    fun isSuccessful(code: Int): Boolean = code in 200..299

    fun isRedirect(code: Int): Boolean =
        code == 301 || code == 302 || code == 307 || code == 308

    fun extractDomain(url: String): String? =
        Regex("^(https?://)?(www\\.)?").replace(url, "")
            .let { domain ->
                val idx = domain.indexOf('/')
                if (idx != -1) domain.substring(0, idx) else domain
            }
            .takeIf { it.isNotEmpty() }

    @Throws(UnsupportedEncodingException::class)
    fun extractQueryParam(url: String, key: String): String? {
        val query = url.substringAfter("?", "")
        if (query.isEmpty()) return null
        return query.split("&").firstNotNullOfOrNull {
            val parts = it.split("=", limit = 2)
            if (parts.size == 2 && parts[0] == key) decodeUrl(parts[1]) else null
        }
    }
}