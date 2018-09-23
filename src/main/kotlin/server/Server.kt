package pl.helenium.mockingbird.server

import pl.helenium.mockingbird.model.HttpMethod

interface Server {

    fun port(): Int

    fun start()

    fun stop()

    fun registerRoute(method: HttpMethod, uri: String, route: Route)

}
