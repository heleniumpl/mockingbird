package pl.helenium.mockingbird.server

import pl.helenium.mockingbird.exception.NotFoundException
import spark.Service

fun Service.configureExceptionHandling() = apply {
    exception(NotFoundException::class.java) { _, _, response ->
        response.apply {
            status(404)
            body("Not found!")
        }
    }
}
