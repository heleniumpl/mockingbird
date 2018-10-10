package pl.helenium.mockingbird.test.mock.getbase

import pl.helenium.mockingbird.definition.Authenticator
import pl.helenium.mockingbird.definition.DslMock
import pl.helenium.mockingbird.definition.identity
import pl.helenium.mockingbird.definition.rest.RestCreateOperation
import pl.helenium.mockingbird.definition.rest.RestDeleteOperation
import pl.helenium.mockingbird.definition.rest.RestGetOperation
import pl.helenium.mockingbird.definition.rest.RestHandler
import pl.helenium.mockingbird.definition.rest.RestListOperation
import pl.helenium.mockingbird.definition.rest.RestUpdateOperation
import pl.helenium.mockingbird.definition.then
import pl.helenium.mockingbird.json.jsonRequestWriter
import pl.helenium.mockingbird.model.Actor
import pl.helenium.mockingbird.model.Authorization
import pl.helenium.mockingbird.model.Context
import pl.helenium.mockingbird.model.LongGenerator
import pl.helenium.mockingbird.model.MetaModel
import pl.helenium.mockingbird.model.Model
import pl.helenium.mockingbird.server.Request

class ContactsMock(context: Context) : DslMock(context, {

    metaModel("contact") {
        properties {
            id().generator(LongGenerator)
        }
        lifecycleHandlers {
            +CreatorLifecycleHandler
            +CreatedUpdatedLifecycleHandler
        }
    }

    authenticator(
        BearerAuthenticator(context, "basePublic")
    )

    handlers {
        post(
            "/v2/contacts",
            RestHandler(
                restOperation = RestCreateOperation(context, metaModel()),
                unwrapper = dataMetaUnwrapper(),
                wrapper = dataMetaWrapper(metaModel()),
                requestWriter = ::jsonRequestWriter
            )
        )

        get(
            "/v2/contacts",
            RestHandler(
                requestParser = ::emptyModel,
                restOperation = RestListOperation(context, metaModel()),
                wrapper = collectionTransformer(dataMetaWrapper(metaModel()))
                        then ::itemsWrapper,
                requestWriter = ::jsonRequestWriter
            )
        )

        get(
            "/v2/contacts/:id",
            RestHandler(
                requestParser = ::emptyModel,
                restOperation = RestGetOperation(context, metaModel()),
                wrapper = dataMetaWrapper(metaModel()),
                requestWriter = ::jsonRequestWriter
            )
        )

        put(
            "/v2/contacts/:id",
            RestHandler(
                restOperation = RestUpdateOperation(context, metaModel()),
                unwrapper = dataMetaUnwrapper(),
                wrapper = dataMetaWrapper(metaModel()),
                requestWriter = ::jsonRequestWriter
            )
        )

        delete(
            "/v2/contacts/:id",
            RestHandler(
                requestParser = ::emptyModel,
                restOperation = RestDeleteOperation(context, metaModel()),
                wrapper = ::identity,
                requestWriter = { "" }
            )
        )
    }

})

private class BearerAuthenticator(
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

private fun emptyModel(@Suppress("UNUSED_PARAMETER") body: String) = Model()

private fun dataMetaUnwrapper(): (Model) -> Model = { it.embeddedModel("data") }

private fun dataMetaWrapper(metaModel: MetaModel): (Model) -> Model = {
    Model(
        mapOf(
            "data" to it.asMap(),
            "meta" to mapOf(
                "type" to metaModel.name
            )
        )
    )
}

private fun itemsWrapper(items: Collection<Model>) = Model(
    mapOf(
        "items" to items,
        "meta" to mapOf(
            "type" to "collection",
            "count" to items.size
        )
    )
)

private fun collectionTransformer(elementTransformer: (Model) -> Model): (Collection<Model>) -> Collection<Model> = {
    it.map(elementTransformer)
}
