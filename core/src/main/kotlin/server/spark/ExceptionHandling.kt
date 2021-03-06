package pl.helenium.mockingbird.server.spark

import pl.helenium.mockingbird.exception.AbstractHttpException
import pl.helenium.mockingbird.exception.getStackTraceAsString
import spark.Service

internal fun Service.configureExceptionHandling() = apply {
    exception(AbstractHttpException::class.java) { exception, _, response ->
        response.apply {
            status(exception.status)
            body(exception.message)
        }
    }

    exception(Exception::class.java) { exception, _, response ->
        response.apply {
            status(500)
            body(exception.getStackTraceAsString())
        }
    }
}
