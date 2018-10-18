package pl.helenium.mockingbird.server

interface Server {

    fun port(): Int

    fun start()

    fun stop()

    fun registerRoute(method: HttpMethod, uri: String, route: Route)

}
