package pl.helenium.mockingbird.server.spark

import mu.KotlinLogging
import spark.Service
import java.util.concurrent.atomic.AtomicLong

private val logger = KotlinLogging.logger {}

private val requestId = AtomicLong(1)

internal fun Service.configureRequestLogging(logRequests: Boolean) = apply {
    if (logRequests) {
        before { request, _ ->
            request.attribute("id", requestId.getAndIncrement())
            logger.info { "IN #${request.attribute<Long>("id")} ${request.requestMethod()} ${request.uri()} <- ${request.body()}" }
        }
        afterAfter { request, response ->
            logger.info { "OUT #${request.attribute<Long>("id")} ${request.requestMethod()} ${request.uri()} -> HTTP ${response.status()} & ${response.body()}" }
        }
    }
}
