package com.github.zyypj.tadeuBooter.api.http.util;

import com.google.gson.Gson;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpUtils {

    private static final Gson gson = new Gson();

    public static String encodeUrl(String raw) throws UnsupportedEncodingException {
        return URLEncoder.encode(raw, String.valueOf(StandardCharsets.UTF_8));
    }

    public static String decodeUrl(String encoded) throws UnsupportedEncodingException {
        return URLDecoder.decode(encoded, String.valueOf(StandardCharsets.UTF_8));
    }

    public static String read(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    public static <T> T readJson(InputStream in, Class<T> clazz) throws IOException {
        String json = read(in);
        return gson.fromJson(json, clazz);
    }

    public static Map<String, String> parseHeaders(HttpURLConnection conn) {
        Map<String, List<String>> headerFields = conn.getHeaderFields();
        Map<String, String> headers = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : headerFields.entrySet()) {
            String key = entry.getKey();
            List<String> value = entry.getValue();
            if (key != null && value != null && !value.isEmpty()) {
                headers.put(key, String.join(", ", value));
            }
        }
        return headers;
    }

    public static String getHeader(HttpURLConnection conn, String key) {
        return conn.getHeaderField(key);
    }

    public static Map<String, String> getCookies(HttpURLConnection conn) {
        Map<String, String> cookies = new HashMap<>();
        List<String> cookieHeaders = conn.getHeaderFields().get("Set-Cookie");
        if (cookieHeaders != null) {
            for (String header : cookieHeaders) {
                String[] pairs = header.split(";");
                for (String pair : pairs) {
                    String[] keyValue = pair.split("=", 2);
                    if (keyValue.length == 2) {
                        cookies.put(keyValue[0].trim(), keyValue[1].trim());
                    }
                }
            }
        }
        return cookies;
    }

    public static boolean isSuccessful(int code) {
        return code >= 200 && code < 300;
    }

    public static boolean isRedirect(int code) {
        return code == 301 || code == 302 || code == 307 || code == 308;
    }

    public static String extractDomain(String url) {
        try {
            String domain = url.replaceFirst("^(https?://)?(www\\.)?", "");
            int slash = domain.indexOf('/');
            return slash != -1 ? domain.substring(0, slash) : domain;
        } catch (Exception e) {
            return null;
        }
    }

    public static String extractQueryParam(String url, String key) throws UnsupportedEncodingException {
        if (!url.contains("?")) return null;
        String[] parts = url.split("\\?");
        if (parts.length < 2) return null;

        String[] params = parts[1].split("&");
        for (String param : params) {
            String[] pair = param.split("=");
            if (pair.length == 2 && pair[0].equals(key)) {
                return decodeUrl(pair[1]);
            }
        }
        return null;
    }
}