package pl.helenium.mockingbird.server

import pl.helenium.mockingbird.model.HttpMethod

interface Server {

    fun port(): Int

    fun start()

    fun stop()

    fun defineRoute(method: HttpMethod, uri: String, route: Route)

}
