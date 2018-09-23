package pl.helenium.mockingbird.model

import pl.helenium.mockingbird.server.Route
import pl.helenium.mockingbird.server.Server

class Routes(private val server: Server) {

    fun register(method: HttpMethod, uri: String, route: Route) = server.registerRoute(method, uri, route)

}
