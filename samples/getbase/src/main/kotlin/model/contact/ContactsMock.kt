package pl.helenium.mockingbird.test.mock.getbase.model.contact

import pl.helenium.mockingbird.definition.DslMock
import pl.helenium.mockingbird.definition.identity
import pl.helenium.mockingbird.definition.rest.EmptyResponseWriter
import pl.helenium.mockingbird.definition.rest.JsonResponseWriter
import pl.helenium.mockingbird.definition.rest.RestCreateOperation
import pl.helenium.mockingbird.definition.rest.RestDeleteOperation
import pl.helenium.mockingbird.definition.rest.RestGetOperation
import pl.helenium.mockingbird.definition.rest.RestHandler
import pl.helenium.mockingbird.definition.rest.RestListOperation
import pl.helenium.mockingbird.definition.rest.RestUpdateOperation
import pl.helenium.mockingbird.definition.rest.emptyModelRequestParser
import pl.helenium.mockingbird.definition.rest.pageTransformer
import pl.helenium.mockingbird.definition.then
import pl.helenium.mockingbird.model.Actor
import pl.helenium.mockingbird.model.Context
import pl.helenium.mockingbird.model.MetaModel
import pl.helenium.mockingbird.model.Model
import pl.helenium.mockingbird.model.ModelError
import pl.helenium.mockingbird.model.Validator
import pl.helenium.mockingbird.model.long
import pl.helenium.mockingbird.model.string
import pl.helenium.mockingbird.test.mock.getbase.BearerAuthenticator
import pl.helenium.mockingbird.test.mock.getbase.dataMetaUnwrapper
import pl.helenium.mockingbird.test.mock.getbase.dataMetaWrapper
import pl.helenium.mockingbird.test.mock.getbase.handler.CreatedUpdatedLifecycleHandler
import pl.helenium.mockingbird.test.mock.getbase.handler.CreatorLifecycleHandler
import pl.helenium.mockingbird.test.mock.getbase.itemsWrapper
import pl.helenium.mockingbird.test.mock.getbase.orderByExtractor
import pl.helenium.mockingbird.test.mock.getbase.pageRequestExtractor

class ContactsMock(context: Context) : DslMock(context, {

    metaModel("contact") {
        properties {
            id {
                type(long)
            }
            property("email") {
                type(string)
            }
        }
        lifecycleHandlers {
            +CreatorLifecycleHandler
            +CreatedUpdatedLifecycleHandler
        }
        validators {
            +ContactNameValidator
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
                unwrapper = ::dataMetaUnwrapper,
                wrapper = dataMetaWrapper(metaModel()),
                responseWriter = JsonResponseWriter
            )
        )

        get(
            "/v2/contacts",
            RestHandler(
                requestParser = ::emptyModelRequestParser,
                restOperation = RestListOperation(
                    context,
                    metaModel(),
                    ::pageRequestExtractor,
                    ::orderByExtractor
                ),
                wrapper = pageTransformer(dataMetaWrapper(metaModel()))
                        then ::itemsWrapper,
                responseWriter = JsonResponseWriter
            )
        )

        get(
            "/v2/contacts/:id",
            RestHandler(
                requestParser = ::emptyModelRequestParser,
                restOperation = RestGetOperation(context, metaModel()),
                wrapper = dataMetaWrapper(metaModel()),
                responseWriter = JsonResponseWriter
            )
        )

        put(
            "/v2/contacts/:id",
            RestHandler(
                restOperation = RestUpdateOperation(context, metaModel()),
                unwrapper = ::dataMetaUnwrapper,
                wrapper = dataMetaWrapper(metaModel()),
                responseWriter = JsonResponseWriter
            )
        )

        delete(
            "/v2/contacts/:id",
            RestHandler(
                requestParser = ::emptyModelRequestParser,
                restOperation = RestDeleteOperation(context, metaModel()),
                wrapper = ::identity,
                responseWriter = EmptyResponseWriter
            )
        )
    }

})

object ContactNameValidator : Validator {

    override fun validate(
        context: Context,
        metaModel: MetaModel,
        actor: Actor?,
        model: Model,
        errors: MutableList<ModelError>
    ) {
        if (model.getProperty("is_organization", false)) {
            if (model.getProperty<String?>("name") == null) {
                errors += "Property 'name' is required for an organization!"
            }
        } else {
            if (model.getProperty<String?>("last_name") == null) {
                errors += "Property 'last_name' is required for an individual!"
            }
        }
    }

}
