package pl.helenium.mockingbird.definition.rest

import pl.helenium.mockingbird.model.Actor
import pl.helenium.mockingbird.model.Context
import pl.helenium.mockingbird.model.MetaModel
import pl.helenium.mockingbird.model.Model
import pl.helenium.mockingbird.server.Request
import pl.helenium.mockingbird.server.Response

class RestCreateOperation(context: Context, metaModel: MetaModel) : RestOperation<Model>(context, metaModel) {

    override fun handle(actor: Actor?, request: Request, response: Response, model: Model) =
        collection().create(model)

}

// FIXME add support for filtering
// FIXME add support for sorting
// FIXME add support for paging
class RestListOperation(context: Context, metaModel: MetaModel) : RestOperation<Collection<Model>>(context, metaModel) {

    override fun handle(actor: Actor?, request: Request, response: Response, model: Model) =
        collection().list()

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
            .update(request.id(), model, RestUpdater)
            ?: notFound(request)
}

class RestDeleteOperation(context: Context, metaModel: MetaModel) : RestOperation<Model>(context, metaModel) {

    override fun handle(actor: Actor?, request: Request, response: Response, model: Model) =
        collection()
            .delete(request.id())
            ?.also { response.status(204) }
            ?: notFound(request)

}
