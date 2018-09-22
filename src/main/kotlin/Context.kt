package pl.helenium.mockingbird

import pl.helenium.mockingbird.model.Actor
import pl.helenium.mockingbird.model.Actors
import pl.helenium.mockingbird.model.Authorization
import pl.helenium.mockingbird.model.MetaModel
import pl.helenium.mockingbird.model.ModelCollection
import spark.Service

interface Context {

    val server: Service

    val port: Int

    val actors: Actors

    fun registerMetaModel(metaModel: MetaModel)

    fun metaModel(name: String): MetaModel

    fun collection(metaModel: MetaModel): ModelCollection

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
