package pl.helenium.mockingbird

import mu.KotlinLogging
import pl.helenium.mockingbird.model.Context
import pl.helenium.mockingbird.model.ContextDsl
import pl.helenium.mockingbird.server.ServerAdapter
import pl.helenium.mockingbird.server.spark.SparkServerAdapter
import kotlin.system.measureTimeMillis

private val logger = KotlinLogging.logger {}

class Mockingbird(private val serverAdapter: ServerAdapter = SparkServerAdapter()) {

    val context = Context(serverAdapter)

    fun setup(dsl: ContextDsl.() -> Unit) = apply { ContextDsl(context).dsl() }

    fun start() = apply {
        try {
            logger.info("Starting Mockingbird server...")
            measureTimeMillis {
                serverAdapter.start()
            }.let { logger.info { "Mockingbird server started on port ${context.port} in ${it}ms" } }
        } catch (e: Exception) {
            logger.error("Failed to start Mockingbird server!", e)
            throw e
        }
    }

    fun stop() = try {
        logger.info("Stopping Mockingbird server...")
        serverAdapter.stop()
    } catch (e: Exception) {
        logger.warn("Failed to stop Mockingbird server!", e)
    }

}
