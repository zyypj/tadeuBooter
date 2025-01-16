package com.github.zyypj.tadeuBooter.http;

import lombok.Generated;

public class RequestResponse<T> {
    private final T rawData;
    private final String data;
    private final Integer statusCode;
    private final Long time;
    private final String headers;
    private final ResponseStatus responseStatus;

    public RequestResponse(T rawData, String data, Integer statusCode, Long time, String headers, ResponseStatus responseStatus) {
        this.rawData = rawData;
        this.data = data;
        this.statusCode = statusCode;
        this.time = time;
        this.headers = headers;
        this.responseStatus = responseStatus;
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

    @Generated
    public Long getTime() {
        return this.time;
    }

    @Generated
    public String getHeaders() {
        return this.headers;
    }

    @Generated
    public ResponseStatus getResponseStatus() {
        return this.responseStatus;
    }
}