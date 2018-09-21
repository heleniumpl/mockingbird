package pl.helenium.mockingbird.test.mock.getbase

import pl.helenium.mockingbird.Context
import pl.helenium.mockingbird.definition.DslMock
import pl.helenium.mockingbird.definition.identity
import pl.helenium.mockingbird.definition.rest.RestCreateHandler
import pl.helenium.mockingbird.definition.rest.RestDeleteHandler
import pl.helenium.mockingbird.definition.rest.RestGetHandler
import pl.helenium.mockingbird.definition.rest.RestListHandler
import pl.helenium.mockingbird.definition.rest.RestRoute
import pl.helenium.mockingbird.definition.rest.RestUpdateHandler
import pl.helenium.mockingbird.definition.then
import pl.helenium.mockingbird.json.jsonRequestWriter
import pl.helenium.mockingbird.model.LongGenerator
import pl.helenium.mockingbird.model.MetaModel
import pl.helenium.mockingbird.model.Model

class ContactsMock(context: Context) : DslMock(context, {

    metaModel("contact") {
        properties {
            id().generator(LongGenerator)
        }
    }

    routes {
        post {
            uri = "/v2/contacts"
            handler = RestRoute(
                context = context,
                metaModel = metaModel,
                restHandler = RestCreateHandler(),
                unwrapper = dataMetaUnwrapper(),
                wrapper = dataMetaWrapper(metaModel),
                requestWriter = ::jsonRequestWriter
            )
        }

        get {
            uri = "/v2/contacts"
            handler = RestRoute(
                context = context,
                metaModel = metaModel,
                requestParser = ::emptyModel,
                restHandler = RestListHandler(),
                wrapper = collectionTransformer(dataMetaWrapper(metaModel))
                        then ::itemsWrapper,
                requestWriter = ::jsonRequestWriter
            )
        }

        get {
            uri = "/v2/contacts/:id"
            handler = RestRoute(
                context = context,
                metaModel = metaModel,
                requestParser = ::emptyModel,
                restHandler = RestGetHandler(),
                wrapper = dataMetaWrapper(metaModel),
                requestWriter = ::jsonRequestWriter
            )
        }

        put {
            uri = "/v2/contacts/:id"
            handler = RestRoute(
                context = context,
                metaModel = metaModel,
                restHandler = RestUpdateHandler(),
                unwrapper = dataMetaUnwrapper(),
                wrapper = dataMetaWrapper(metaModel),
                requestWriter = ::jsonRequestWriter
            )
        }

        delete {
            uri = "/v2/contacts/:id"
            handler = RestRoute(
                context = context,
                metaModel = metaModel,
                requestParser = ::emptyModel,
                restHandler = RestDeleteHandler(),
                wrapper = ::identity,
                requestWriter = { "" }
            )
        }
    }

})

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
