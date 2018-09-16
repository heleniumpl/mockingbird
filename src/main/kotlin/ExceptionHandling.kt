package pl.helenium.mockingbird

import spark.Service

fun Service.configureExceptionHandling() = apply {
    this.exception(NotFoundException::class.java) { _, _, response ->
        response.status(404)
        response.body("Not found!")
    }
}
