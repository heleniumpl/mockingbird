package pl.helenium.mockingbird

import mu.KotlinLogging
import pl.helenium.mockingbird.model.Actors
import pl.helenium.mockingbird.model.Context
import pl.helenium.mockingbird.model.ContextDsl
import pl.helenium.mockingbird.model.MetaModels
import pl.helenium.mockingbird.model.ModelCollections
import pl.helenium.mockingbird.server.configureExceptionHandling
import pl.helenium.mockingbird.server.configureRequestLogging
import spark.Service
import spark.Service.ignite
import kotlin.system.measureTimeMillis

private val logger = KotlinLogging.logger {}

class Mockingbird(
    port: Int = 0,
    private val logRequests: Boolean = true
) {

    private val server: Service = ignite().port(port)

    val context = object : Context {

        override val server = this@Mockingbird.server

        override val port by lazy { server.port() }

        override val actors = Actors()

        override val metaModels = MetaModels()

        override val modelCollections = ModelCollections()

    }

    fun setup(dsl: ContextDsl.() -> Unit) = apply { ContextDsl(context).dsl() }

    fun start() = apply {
        try {
            logger.info("Starting Mockingbird server...")
            measureTimeMillis {
                server
                    .apply {
                        configureRequestLogging(logRequests)
                        init()
                        awaitInitialization()
                        configureExceptionHandling()
                    }
            }.let { logger.info { "Mockingbird server started on port ${context.server.port()} in ${it}ms" } }
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
