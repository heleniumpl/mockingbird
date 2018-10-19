package pl.helenium.mockingbird.test.mock.getbase.model.deal

import pl.helenium.mockingbird.definition.DslMock
import pl.helenium.mockingbird.definition.rest.RestCreateOperation
import pl.helenium.mockingbird.definition.rest.RestHandler
import pl.helenium.mockingbird.json.jsonRequestWriter
import pl.helenium.mockingbird.model.Context
import pl.helenium.mockingbird.model.long
import pl.helenium.mockingbird.test.mock.getbase.BearerAuthenticator
import pl.helenium.mockingbird.test.mock.getbase.dataMetaUnwrapper
import pl.helenium.mockingbird.test.mock.getbase.dataMetaWrapper
import pl.helenium.mockingbird.test.mock.getbase.handler.CreatedUpdatedLifecycleHandler
import pl.helenium.mockingbird.test.mock.getbase.handler.CreatorLifecycleHandler

class DealsMock(context: Context) : DslMock(context, {

    metaModel("deal") {
        properties {
            id {
                type(long)
            }
            property("name") {
                required()
            }
            property("contact_id") {
                type(long)
                required()
            }
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
            "/v2/deals",
            RestHandler(
                restOperation = RestCreateOperation(context, metaModel()),
                unwrapper = dataMetaUnwrapper(),
                wrapper = dataMetaWrapper(metaModel()),
                requestWriter = ::jsonRequestWriter
            )
        )
    }

})
