package com.github.zyypj.tadeuBooter.api.http.client

/**
 * Enum que mapeia códigos HTTP para um estado semântico simples.
 */
enum class HttpStatus {
    SUCCESS,
    ACCEPTED,
    MOVED_PERMANENTLY,
    BAD_REQUEST,
    UNAUTHORIZED,
    FORBIDDEN,
    NOT_FOUND,
    INTERNAL_SERVER_ERROR,
    NOT_IMPLEMENTED,
    SERVICE_UNAVAILABLE,
    GATEWAY_TIMEOUT,
    UNKNOWN;

    companion object {
        /** Converte o código numérico em um {@link HttpStatus}. */
        @JvmStatic
        fun get(code: Int): HttpStatus = when (code) {
            200 -> SUCCESS
            202 -> ACCEPTED
            301 -> MOVED_PERMANENTLY
            400 -> BAD_REQUEST
            401 -> UNAUTHORIZED
            403 -> FORBIDDEN
            404 -> NOT_FOUND
            500 -> INTERNAL_SERVER_ERROR
            501 -> NOT_IMPLEMENTED
            503 -> SERVICE_UNAVAILABLE
            504 -> GATEWAY_TIMEOUT
            else -> UNKNOWN
        }
    }
}