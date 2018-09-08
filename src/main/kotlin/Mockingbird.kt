package pl.helenium.mockingbird

import io.undertow.Undertow
import io.undertow.util.Headers.CONTENT_TYPE
import mu.KotlinLogging
import java.net.InetSocketAddress

private val logger = KotlinLogging.logger {}

class Mockingbird(port: Int = 0) {

    private val server: Undertow = Undertow
        .builder()
        .addHttpListener(port, "localhost")
        .setHandler { exchange ->
            exchange.run {
                responseHeaders.put(CONTENT_TYPE, "text/plain")
                statusCode = 404
                responseSender.send("Hello World")
            }
        }.build()

    fun start() = try {
        logger.info("Starting Mockingbird server...")
        server.start()
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

    fun port() = server
        .listenerInfo
        .first()
        .address
        .let { it as InetSocketAddress }
        .port

}
