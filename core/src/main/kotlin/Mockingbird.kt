package pl.helenium.mockingbird

import mu.KotlinLogging
import pl.helenium.mockingbird.model.Context
import pl.helenium.mockingbird.model.Context.ContextDsl
import pl.helenium.mockingbird.server.Server
import pl.helenium.mockingbird.server.spark.SparkServer
import kotlin.system.measureTimeMillis

private val logger = KotlinLogging.logger {}

class Mockingbird(private val server: Server = SparkServer(), buildBlock: ContextDsl.() -> Unit = {}) {

    val context = Context(server, buildBlock)

    fun start() = apply {
        try {
            logger.info("Starting Mockingbird server...")
            measureTimeMillis {
                server.start()
            }.let { logger.info { "Mockingbird server started on port ${context.port} in ${it}ms" } }
        } catch (e: Exception) {
            logger.error("Failed to start Mockingbird server!", e)
            throw e
        }
    }

    fun stop() = try {
        logger.info("Stopping Mockingbird server...")
        server.stop()
    } catch (e: Exception) {
        logger.warn("Failed to stop Mockingbird server!", e)
    }

}
