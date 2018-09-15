package pl.helenium.mockingbird.test.mock.getbase

import pl.helenium.mockingbird.*

class ContactsMock(context: Context) : DslMock(context, {

    metaModel("contact") {
        properties {
            id("id")
                .generator(LongGenerator)
        }
    }

    post(
        "/v2/contacts",
        Rest(
            context = context,
            metaModel = metaModel,
            restHandler = RestCreateHandler(),
            unwrapper = { it.embeddedModel("data") },
            wrapper = {
                Model(
                    mapOf(
                        "data" to it.asMap(),
                        "meta" to mapOf(
                            "type" to metaModel.name
                        )
                    )
                )
            },
            requestWriter = ::jsonRequestWriter
        )
    )

})
