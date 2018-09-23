package pl.helenium.mockingbird.model

import pl.helenium.mockingbird.definition.Authenticator
import pl.helenium.mockingbird.exception.unauthorized
import pl.helenium.mockingbird.server.Request
import pl.helenium.mockingbird.server.Response
import pl.helenium.mockingbird.server.Route
import pl.helenium.mockingbird.server.Server

class Handlers(private val server: Server) {

    fun register(
        method: HttpMethod,
        uri: String,
        handler: Handler,
        authenticator: Authenticator?
    ) = server.registerRoute(method, uri, route(handler, authenticator))

    private fun route(handler: Handler, authenticator: Authenticator?): Route = { request, response ->
        val actor = if (authenticator != null) {
            authenticator(request) ?: unauthorized()
        } else {
            null
        }
        handler(actor, request, response)
    }

}

typealias Handler = (actor: Actor?, request: Request, response: Response) -> Any?
