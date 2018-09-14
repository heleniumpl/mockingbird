package pl.helenium.mockingbird.test.mock.getbase

import pl.helenium.mockingbird.Context
import pl.helenium.mockingbird.Create
import pl.helenium.mockingbird.DslMock
import pl.helenium.mockingbird.Model

class ContactsMock(context: Context) : DslMock(context, {

    metaModel("contact") {
        properties {
            id("id")
        }
    }

    post("/v2/contacts") {
        Create(
            context,
            metaModel,
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
            }
        )
    }

})
