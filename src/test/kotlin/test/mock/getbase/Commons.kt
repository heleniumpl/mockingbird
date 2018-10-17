package pl.helenium.mockingbird.test.mock.getbase

import pl.helenium.mockingbird.definition.Authenticator
import pl.helenium.mockingbird.model.Actor
import pl.helenium.mockingbird.model.Authorization
import pl.helenium.mockingbird.model.Context
import pl.helenium.mockingbird.model.MetaModel
import pl.helenium.mockingbird.model.Model
import pl.helenium.mockingbird.server.Request

class BearerAuthenticator(
    private val context: Context,
    private val scope: String
) : Authenticator {

    override fun invoke(request: Request): Actor? = context
        .actors
        .scope(scope)
        .authorize(request.authorization())

    private fun Request.authorization() = header("Authorization")
        ?.takeIf { it.startsWith("Bearer ") }
        ?.removePrefix("Bearer ")
        ?.let(::Authorization)

}

fun dataMetaUnwrapper(): (Model) -> Model = { it.embeddedModel("data") }

fun dataMetaWrapper(metaModel: MetaModel): (Model) -> Model = {
    Model(
        mapOf(
            "data" to it.asMap(),
            "meta" to mapOf(
                "type" to metaModel.name
            )
        )
    )
}

fun itemsWrapper(items: Collection<Model>) = Model(
    mapOf(
        "items" to items,
        "meta" to mapOf(
            "type" to "collection",
            "count" to items.size
        )
    )
)
