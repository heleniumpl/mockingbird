package pl.helenium.mockingbird.test.commons

import com.github.kittinunf.fuel.core.Request
import kotlin.text.Charsets.UTF_8

data class StatusAndBody(val status: Int, val body: String)

fun Request.execute() = response().second.run {
    StatusAndBody(
        statusCode,
        data.toString(UTF_8)
    )
}
