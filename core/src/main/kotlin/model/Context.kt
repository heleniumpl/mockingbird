package pl.helenium.mockingbird.model

import pl.helenium.mockingbird.model.Actors.Scope.ScopeDsl
import pl.helenium.mockingbird.server.Server

class Context(private val server: Server, buildBlock: ContextDsl.() -> Unit = {}) {

    val port: Int
        get() = server.port()

    val actors = Actors()

    val metaModels = MetaModels()

    val modelCollections = ModelCollections(this)

    val handlers = Handlers(server)

    val services = Services()

    init {
        ContextDsl().buildBlock()
    }

    inner class ContextDsl {

        // FIXME rethink the way mocks are registered
        fun mocks(vararg registrants: Context.() -> Any?) =
            registrants.forEach { registrant ->
                registrant()
            }

        fun actors(buildBlock: ActorsDsl.() -> Unit) = ActorsDsl().buildBlock()

        inner class ActorsDsl {

            fun scope(scope: String, buildBlock: ScopeDsl.() -> Unit) = actors
                .scope(scope, buildBlock)

        }

    }

}
