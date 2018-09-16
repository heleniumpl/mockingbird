package pl.helenium.mockingbird

import mu.KotlinLogging
import spark.Service.ignite
import kotlin.system.measureTimeMillis

private val logger = KotlinLogging.logger {}

class Mockingbird(
    port: Int = 0,
    private val logRequests: Boolean = true
) {

    private val server = ignite().port(port)!!

    private val metaModels = mutableMapOf<String, MetaModel>()

    private val modelCollections = mutableMapOf<MetaModel, ModelCollection>()

    val context = object : Context {

        override val server = this@Mockingbird.server

        override val port by lazy { server.port() }

        override fun registerMetaModel(metaModel: MetaModel) {
            metaModels[metaModel.name] = metaModel
        }

        override fun metaModel(name: String) =
            metaModels[name] ?: throw IllegalArgumentException("No MetaModel could be found for name $name!")

        override fun collection(metaModel: MetaModel) =
            modelCollections.computeIfAbsent(metaModel, ::ModelCollection)

    }

    fun mocks(vararg registrants: (Context) -> Any?) = apply {
        registrants.forEach { registrant ->
            registrant(context)
        }
    }

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
