package pl.helenium.mockingbird.model

class Actors {

    private val scopes = mutableMapOf<String, Scope>()

    fun scope(scope: String) = scopes.computeIfAbsent(scope) { Scope() }

    class Scope internal constructor() {

        private val actors = mutableSetOf<Actor>()

        fun register(actor: Actor) {
            actors.add(actor)
        }

        fun authorize(authorization: Authorization?): Actor? {
            if (authorization == null) return null

            return actors
                .asSequence()
                .find { it.authorization.matches(authorization) }
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
