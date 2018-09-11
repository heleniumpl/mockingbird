package pl.helenium.mockingbird

import mu.KotlinLogging
import spark.Service
import spark.Service.ignite

private val logger = KotlinLogging.logger {}

class Mockingbird(port: Int = 0) {

    private val server: Service = ignite()
        .port(port)

    fun start() = try {
        logger.info("Starting Mockingbird server...")
        server.run {
            init()
            awaitInitialization()
        }
        logger.info { "Mockingbird server started on port ${port()}" }
    } catch (e: Exception) {
        logger.error("Failed to start Mockingbird server!", e)
        throw e
    }

    fun stop() = try {
        logger.info("Stopping Mockingbird server...")
        server.stop()
    } catch (e: Exception) {
        logger.warn("Failed to stop Mockingbird server!", e)
    }

    fun port() = server.port()

}
