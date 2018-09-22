package pl.helenium.mockingbird.model

import spark.Service

interface Context {

    val server: Service

    val port: Int

    val actors: Actors

    val metaModels: MetaModels

    val modelCollections: ModelCollections

}

class ContextDsl(private val context: Context) {

    // FIXME rethink the way mocks are registered
    fun mocks(vararg registrants: (Context) -> Any?) = apply {
        registrants.forEach { registrant ->
            registrant(context)
        }
    }

    fun actors(dsl: ScopesDsl.() -> Unit) = apply {
        ScopesDsl(context).dsl()
    }

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
