package com.github.zyypj.booter.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Request<T> {
    private HttpURLConnection requestConnection;
    private RequestResponse<T> response;
    private String contentType;
    private Exception exception;
    private long requestTime;
    private boolean requireSSL = true;
    private final URL url;
    private int timeout;

    /**
     * Construtor que recebe uma URL em formato de string e inicializa o objeto Request.
     *
     * @param url A URL do endpoint a ser chamado.
     */
    public Request(String url) {
        try {
            this.url = new URL(url);
        } catch (MalformedURLException var3) {
            throw new RuntimeException("A url provida é inválida: " + var3);
        }
    }

    /**
     * Construtor que recebe uma URL já criada e inicializa o objeto Request.
     *
     * @param url A URL do endpoint a ser chamado.
     */
    public Request(URL url) {
        this.url = url;
    }

    /**
     * Construtor que recebe uma URL e um tempo limite de conexão.
     *
     * @param url              A URL do endpoint a ser chamado.
     * @param timeoutInMillis  Tempo limite em milissegundos para a conexão.
     */
    public Request(URL url, int timeoutInMillis) {
        this.url = url;
        this.timeout = timeoutInMillis;
    }

    /**
     * Executa a requisição HTTP configurada.
     *
     * @return true se a requisição foi executada com sucesso, false caso contrário.
     */
    public boolean execute() {
        try {
            long now = System.currentTimeMillis();
            int timeout = this.timeout == 0 ? 5000 : this.timeout;
            this.requestConnection = (HttpURLConnection) this.url.openConnection();
            this.requestConnection.setRequestMethod("GET");
            this.requestConnection.setRequestProperty("requireSSL", String.valueOf(this.requireSSL));
            this.requestConnection.setConnectTimeout(timeout);
            if (this.contentType != null && !this.contentType.isEmpty()) {
                this.requestConnection.setRequestProperty("Content-Type", this.contentType);
            }

            this.requestConnection.connect();
            this.requestTime = System.currentTimeMillis() - now;
            this.buildResponse();
            return true;
        } catch (IOException | RuntimeException var4) {
            this.exception = var4;
            return false;
        }
    }

    /**
     * Constrói a resposta da requisição, incluindo código de status, cabeçalhos e corpo da resposta.
     *
     * @throws IOException Caso ocorra um erro ao ler a resposta.
     */
    private void buildResponse() throws IOException {
        int responseCode = this.requestConnection.getResponseCode();
        long responseTime = this.requestTime;
        String headers = this.requestConnection.getHeaderFields().toString();

        BufferedReader in = new BufferedReader(new InputStreamReader(this.requestConnection.getInputStream()));
        StringBuilder raw = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            raw.append(inputLine);
        }
        in.close();

        String data = raw.toString();

        this.response = new RequestResponse(raw.toString(), data, responseCode, responseTime, headers, ResponseStatus.get(responseCode));
    }

    @Generated
    public HttpURLConnection getRequestConnection() {
        return this.requestConnection;
    }

    @Generated
    public String getContentType() {
        return this.contentType;
    }

    @Generated
    public long getRequestTime() {
        return this.requestTime;
    }

    @Generated
    public boolean isRequireSSL() {
        return this.requireSSL;
    }

    @Generated
    public URL getUrl() {
        return this.url;
    }

    @Generated
    public int getTimeout() {
        return this.timeout;
    }

    @Generated
    public Exception getException() {
        return this.exception;
    }
}