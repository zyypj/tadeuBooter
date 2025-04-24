package me.zyypj.booter.shared.http.client

import java.io.BufferedReader
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

/**
 * Cliente HTTP enxuto (GET) com timeout configurável.
 *
 * @param T Tipo genérico associado ao dado bruto retornado pela requisição.
 */
class HttpRequest<T> @JvmOverloads constructor(
    /** URL do endpoint. */
    val url: URL,
    /** Tempo‑limite da conexão (ms). */
    var timeout: Int = 0,
) {

    /** Conexão HTTP aberta durante a execução. */
    var requestConnection: HttpURLConnection? = null
        private set

    /** Objeto de resposta preenchido após execução bem‑sucedida. */
    var response: HttpResponse<T>? = null
        private set

    /** Cabeçalho *Content‑Type* enviado junto à requisição (opcional). */
    var contentType: String? = null

    /** Exceção capturada, caso a execução falhe. */
    var exception: Exception? = null
        private set

    /** Tempo total da requisição em milissegundos. */
    var requestTime: Long = 0
        private set

    /** Define se certificados SSL devem ser exigidos (apenas informativo). */
    var requireSSL: Boolean = true

    /**
     * Construtor auxiliar que aceita a URL em formato de *String*.
     * @throws RuntimeException se o endereço informado não for um URL válido.
     */
    constructor(url: String) : this(parseUrl(url))


    /**
     * Executa a requisição **GET** e preenche [response].
     *
     * @return `true` se a operação concluir sem exceções; do contrário, `false`,
     *         e a propriedade [exception] conterá o erro ocorrido.
     */
    fun execute(): Boolean {
        return try {
            val start = System.currentTimeMillis()
            val effectiveTimeout = if (timeout == 0) 5_000 else timeout

            requestConnection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("requireSSL", requireSSL.toString())
                connectTimeout = effectiveTimeout
                contentType?.takeIf { it.isNotEmpty() }?.let { setRequestProperty("Content-Type", it) }
                connect()
            }

            requestTime = System.currentTimeMillis() - start
            buildResponse()
            true
        } catch (ex: IOException) {
            exception = ex
            false
        } catch (ex: RuntimeException) {
            exception = ex
            false
        }
    }

    /**
     * Monta a instância de [HttpResponse] a partir do **status code**, cabeçalhos e corpo
     * retornados pelo servidor.
     *
     * @throws IOException Caso ocorra falha na leitura do *input stream*.
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(IOException::class)
    private fun buildResponse() {
        val conn = requestConnection ?: return
        val responseCode = conn.responseCode
        val headers = conn.headerFields.toString()
        val body = conn.inputStream.bufferedReader().use(BufferedReader::readText)

        response = HttpResponse(
            rawData = body as T, // se T = String, cast é seguro
            data = body,
            statusCode = responseCode,
            time = requestTime,
            headers = headers,
            httpStatus = HttpStatus.get(responseCode)
        )
    }

    companion object {
        /**
         * Converte uma *String* para [URL], lançando exceção caso seja inválida.
         *
         * @param value Endereço a ser convertido.
         * @throws RuntimeException Se o valor não representar uma URL válida.
         */
        @JvmStatic
        private fun parseUrl(value: String): URL = try {
            URL(value)
        } catch (ex: MalformedURLException) {
            throw RuntimeException("A url provida é inválida: $ex")
        }
    }
}