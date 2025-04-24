package me.zyypj.booter.shared.http.client


/**
 * Representa a resposta de uma requisição HTTP.
 *
 * @param rawData     dado bruto (tipado) obtido da requisição – útil quando você deseja
 *                    trabalhar com JSON já desserializado, por exemplo.
 * @param data        corpo em texto puro.
 * @param statusCode  código de status HTTP.
 * @param time        tempo de resposta em milissegundos.
 * @param headers     cabeçalhos em string.
 * @param httpStatus  enum simplificado mapeado pelo código.
 */
data class HttpResponse<T>(
    val rawData: T,
    val data: String,
    val statusCode: Int,
    val time: Long,
    val headers: String,
    val httpStatus: HttpStatus,
)