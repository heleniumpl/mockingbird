package pl.helenium.mockingbird.model

import pl.helenium.mockingbird.model.Actors.Scope
import pl.helenium.mockingbird.server.Server

class Context(private val server: Server) {

    val port: Int
        get() = server.port()

    val actors = Actors()

    val metaModels = MetaModels()

    val modelCollections = ModelCollections()

    val handlers = Handlers(server)

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

    class ScopeDsl(private val scope: Scope) {

        fun actor(id: String, authorization: String, name: String = id) {
            scope.register(Actor(id, Authorization(authorization), name))
        }

    }

}
