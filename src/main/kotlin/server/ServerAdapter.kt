package pl.helenium.mockingbird.server

import pl.helenium.mockingbird.model.HttpMethod
import spark.Route

interface ServerAdapter {

    fun port(): Int

    fun start()

    fun stop()

    fun defineRoute(method: HttpMethod, uri: String, route: Route)

}
