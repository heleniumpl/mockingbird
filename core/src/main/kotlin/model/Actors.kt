package pl.helenium.mockingbird.model

import pl.helenium.mockingbird.model.Actors.Scope.ScopeDsl

class Actors {

    private val scopes = mutableMapOf<String, Scope>()

    fun scope(scope: String, buildBlock: ScopeDsl.() -> Unit = {}) = scopes.computeIfAbsent(scope) { Scope(buildBlock) }

    class Scope internal constructor(buildBlock: ScopeDsl.() -> Unit) {

        private val actors = mutableSetOf<Actor>()

        init {
            ScopeDsl().buildBlock()
        }

        fun register(actor: Actor) {
            actors.add(actor)
        }

        fun authorize(authorization: Authorization?): Actor? {
            if (authorization == null) return null

            return actors
                .asSequence()
                .find { it.authorization.matches(authorization) }
        }

        inner class ScopeDsl {

            fun actor(id: Any, authorization: String, name: String = id.toString()) {
                register(Actor(id, Authorization(authorization), name))
            }

        }

    }

}

data class Actor(
    val id: Any,
    val authorization: Authorization,
    val name: String
)

class Authorization(private val credentials: String) {

    fun matches(authorization: Authorization) =
        credentials == authorization.credentials

}
