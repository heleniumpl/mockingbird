package pl.helenium.mockingbird

import mu.KotlinLogging
import spark.Service.ignite
import kotlin.system.measureTimeMillis

private val logger = KotlinLogging.logger {}

class Mockingbird(
    port: Int = 0,
    logRequests: Boolean = true
) {

    private val server = ignite()
        .port(port)!!
        .apply(configureRequestLogging(logRequests))

    private val metaModels = mutableMapOf<String, MetaModel>()

    private val modelCollections = mutableMapOf<MetaModel, ModelCollection>()

    val context = object : Context {

        override val server = this@Mockingbird.server

        override val port = server.port()

        override fun registerMetaModel(metaModel: MetaModel) {
            metaModels[metaModel.name] = metaModel
        }

        override fun metaModel(name: String) =
            metaModels[name] ?: throw IllegalArgumentException("No MetaModel could be found for name $name!")

        override fun collection(metaModel: MetaModel) =
            modelCollections.computeIfAbsent(metaModel, ::ModelCollection)

    }

    fun mocks(vararg registrants: (Context) -> Any?) = also {
        registrants.forEach { registrant ->
            registrant(context)
        }
    }

    fun start(): Mockingbird = try {
        logger.info("Starting Mockingbird server...")
        measureTimeMillis {
            context
                .server
                .run {
                    init()
                    awaitInitialization()
                }
        }.let { logger.info { "Mockingbird server started on port ${context.server.port()} in ${it}ms" } }
        this
    } catch (e: Exception) {
        logger.error("Failed to start Mockingbird server!", e)
        throw e
    }

    fun stop() = try {
        logger.info("Stopping Mockingbird server...")
        context
            .server
            .stop()
    } catch (e: Exception) {
        logger.warn("Failed to stop Mockingbird server!", e)
    }

}
