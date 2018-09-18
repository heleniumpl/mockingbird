package pl.helenium.mockingbird.test.mock.getbase

import pl.helenium.mockingbird.*

class ContactsMock(context: Context) : DslMock(context, {

    metaModel("contact") {
        properties {
            id().generator(LongGenerator)
        }
    }

    routes {
        post {
            uri = "/v2/contacts"
            handler = Rest(
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
            handler = Rest(
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
            handler = Rest(
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
            handler = Rest(
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
            handler = Rest(
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
