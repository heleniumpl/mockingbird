package pl.helenium.mockingbird

import mu.KotlinLogging
import spark.Service.ignite
import kotlin.system.measureTimeMillis

private val logger = KotlinLogging.logger {}

class Mockingbird(port: Int = 0) : Context {

    override val server = ignite()
        .port(port)!!

    fun mocks(vararg mocks: Mock) = also {
        mocks.forEach { mock ->
            mock.register()(this)
        }
    }

    fun start(): Mockingbird = try {
        logger.info("Starting Mockingbird server...")
        measureTimeMillis {
            server.run {
                init()
                awaitInitialization()
            }
        }.let { logger.info { "Mockingbird server started on port ${server.port()} in ${it}ms" } }
        this
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

}
