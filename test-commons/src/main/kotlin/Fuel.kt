package pl.helenium.mockingbird.test.commons

import com.github.kittinunf.fuel.core.Request
import kotlin.text.Charsets.UTF_8

data class Response(
    val status: Int,
    val headers: Map<String, List<String>>,
    val body: String
) {

    fun contentType() = headers["Content-Type"]?.first()

}

fun Request.execute() = response().second.run {
    Response(
        statusCode,
        headers,
        data.toString(UTF_8)
    )
}
