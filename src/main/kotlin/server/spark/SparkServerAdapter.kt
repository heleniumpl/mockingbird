package pl.helenium.mockingbird.server.spark

import pl.helenium.mockingbird.model.HttpMethod
import pl.helenium.mockingbird.model.HttpMethod.DELETE
import pl.helenium.mockingbird.model.HttpMethod.GET
import pl.helenium.mockingbird.model.HttpMethod.POST
import pl.helenium.mockingbird.model.HttpMethod.PUT
import pl.helenium.mockingbird.server.ServerAdapter
import spark.Route
import spark.Service
import spark.Service.ignite

class SparkServerAdapter(
    port: Int = 0,
    private val logRequests: Boolean = true
) : ServerAdapter {

    private val service: Service = ignite().port(port)

    override fun port() = service.port()

    override fun start() {
        service.apply {
            configureRequestLogging(logRequests)
            init()
            awaitInitialization()
            configureExceptionHandling()
        }
    }

    override fun stop() {
        service.stop()
    }

    override fun defineRoute(method: HttpMethod, uri: String, route: Route) = when (method) {
        POST -> service.post(uri, route)
        GET -> service.get(uri, route)
        PUT -> service.put(uri, route)
        DELETE -> service.delete(uri, route)
    }

}
