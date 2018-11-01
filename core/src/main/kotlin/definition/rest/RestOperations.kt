package pl.helenium.mockingbird.definition.rest

import pl.helenium.mockingbird.model.Actor
import pl.helenium.mockingbird.model.Context
import pl.helenium.mockingbird.model.MetaModel
import pl.helenium.mockingbird.model.Model
import pl.helenium.mockingbird.model.OrderBy
import pl.helenium.mockingbird.model.Page
import pl.helenium.mockingbird.model.PageRequest
import pl.helenium.mockingbird.server.Request
import pl.helenium.mockingbird.server.Response

class RestCreateOperation(context: Context, metaModel: MetaModel) : RestOperation<Model>(context, metaModel) {

    override fun handle(actor: Actor?, request: Request, response: Response, model: Model) =
        collection().create(actor, model)

}

// FIXME add support for filtering
class RestListOperation(
    context: Context,
    metaModel: MetaModel,
    private val pageRequestExtractor: (Request) -> PageRequest? = { null },
    private val orderByExtractor: (MetaModel, Request) -> OrderBy? = { _, _ -> null }
) : RestOperation<Page<Model>>(context, metaModel) {

    override fun handle(actor: Actor?, request: Request, response: Response, model: Model) =
        collection().list(
            pageRequestExtractor(request),
            orderByExtractor(metaModel, request)
        )

}

class RestGetOperation(context: Context, metaModel: MetaModel) : RestOperation<Model>(context, metaModel) {

    override fun handle(actor: Actor?, request: Request, response: Response, model: Model) =
        collection()
            .get(request.id())
            ?: notFound(request)

}

class RestUpdateOperation(context: Context, metaModel: MetaModel) : RestOperation<Model>(context, metaModel) {

    override fun handle(actor: Actor?, request: Request, response: Response, model: Model) =
        collection()
            .update(actor, request.id(), model, RestUpdater)
            ?: notFound(request)
}

class RestDeleteOperation(context: Context, metaModel: MetaModel) : RestOperation<Model>(context, metaModel) {

    override fun handle(actor: Actor?, request: Request, response: Response, model: Model) =
        collection()
            .delete(actor, request.id())
            ?.also { response.status(204) }
            ?: notFound(request)

}
