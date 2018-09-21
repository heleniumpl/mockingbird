package pl.helenium.mockingbird.server

import pl.helenium.mockingbird.exception.AbstractHttpException
import spark.Service

fun Service.configureExceptionHandling() = apply {
    exception(AbstractHttpException::class.java) { exception, _, response ->
        response.apply {
            status(exception.status)
            body(exception.message)
        }
    }
}
