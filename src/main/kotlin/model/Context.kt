package pl.helenium.mockingbird.model

import pl.helenium.mockingbird.server.Route
import pl.helenium.mockingbird.server.Server

class Context(private val server: Server) {

    val port: Int
        get() = server.port()

    val actors = Actors()

    val metaModels = MetaModels()

    val modelCollections = ModelCollections()

    // FIXME should be Routes
    // FIXME should not depend on spark.Route
    // FIXME some enum should be used for method
    fun defineRoute(method: HttpMethod, uri: String, route: Route) {
        server.defineRoute(method, uri, route)
    }

}

class ContextDsl(private val context: Context) {

    // FIXME rethink the way mocks are registered
    fun mocks(vararg registrants: (Context) -> Any?) =
        registrants.forEach { registrant ->
            registrant(context)
        }

    fun actors(dsl: ScopesDsl.() -> Unit) = ScopesDsl(context).dsl()

    class ScopesDsl(private val context: Context) {

        fun scope(scope: String, dsl: ScopeDsl.() -> Unit) = context
            .actors
            .scope(scope)
            .let(::ScopeDsl)
            .dsl()

    }

    class ScopeDsl(private val scope: Actors.Scope) {

        fun actor(id: String, authorization: String, name: String = id) {
            scope.register(Actor(id, Authorization(authorization), name))
        }

    }

}
