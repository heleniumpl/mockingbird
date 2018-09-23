package pl.helenium.mockingbird.server.spark

import pl.helenium.mockingbird.model.HttpMethod
import pl.helenium.mockingbird.model.HttpMethod.DELETE
import pl.helenium.mockingbird.model.HttpMethod.GET
import pl.helenium.mockingbird.model.HttpMethod.POST
import pl.helenium.mockingbird.model.HttpMethod.PUT
import pl.helenium.mockingbird.server.Route
import pl.helenium.mockingbird.server.Server
import spark.Service
import spark.Service.ignite
import spark.Route as SparkRoute

class SparkServer(
    port: Int = 0,
    private val logRequests: Boolean = true
) : Server {

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

    override fun defineRoute(method: HttpMethod, uri: String, route: Route) {
        val internalRoute = SparkRoute { request, response ->
            route(SparkRequest(request), SparkResponse(response))
        }
        return when (method) {
            POST -> service.post(uri, internalRoute)
            GET -> service.get(uri, internalRoute)
            PUT -> service.put(uri, internalRoute)
            DELETE -> service.delete(uri, internalRoute)
        }
    }

}
