package pl.helenium.mockingbird.test.mock.getbase

import pl.helenium.mockingbird.model.MetaModel
import pl.helenium.mockingbird.model.Model

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
