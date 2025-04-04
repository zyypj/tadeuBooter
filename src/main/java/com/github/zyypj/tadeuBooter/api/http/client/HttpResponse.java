package com.github.zyypj.tadeuBooter.api.http.client;

import lombok.Generated;

public class HttpResponse<T> {
    private final T rawData;
    private final String data;
    private final Integer statusCode;
    private final Long time;
    private final String headers;
    private final HttpStatus httpStatus;

    public HttpResponse(T rawData, String data, Integer statusCode, Long time, String headers, HttpStatus httpStatus) {
        this.rawData = rawData;
        this.data = data;
        this.statusCode = statusCode;
        this.time = time;
        this.headers = headers;
        this.httpStatus = httpStatus;
    }

    @Generated
    public T getRawData() {
        return this.rawData;
    }

    @Generated
    public String getData() {
        return this.data;
    }

    @Generated
    public Integer getStatusCode() {
        return this.statusCode;
    }
}