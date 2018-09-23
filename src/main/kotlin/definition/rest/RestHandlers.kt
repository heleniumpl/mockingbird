package pl.helenium.mockingbird.definition.rest

import pl.helenium.mockingbird.model.Context
import pl.helenium.mockingbird.model.MetaModel
import pl.helenium.mockingbird.model.Model
import pl.helenium.mockingbird.server.Request
import pl.helenium.mockingbird.server.Response

class RestCreateHandler(context: Context, metaModel: MetaModel) : RestHandler<Model>(context, metaModel) {

    override fun handle(request: Request, response: Response, model: Model) = collection().create(model)

}

// FIXME add support for filtering
// FIXME add support for sorting
// FIXME add support for paging
class RestListHandler(context: Context, metaModel: MetaModel) : RestHandler<Collection<Model>>(context, metaModel) {

    override fun handle(request: Request, response: Response, model: Model) = collection().list()

}

class RestGetHandler(context: Context, metaModel: MetaModel) : RestHandler<Model>(context, metaModel) {

    override fun handle(request: Request, response: Response, model: Model) =
        collection()
            .get(request.id())
            ?: notFound(request)

}

class RestUpdateHandler(context: Context, metaModel: MetaModel) : RestHandler<Model>(context, metaModel) {

    override fun handle(request: Request, response: Response, model: Model) =
        collection()
            .update(request.id(), model, RestUpdater)
            ?: notFound(request)
}

class RestDeleteHandler(context: Context, metaModel: MetaModel) : RestHandler<Model>(context, metaModel) {

    override fun handle(request: Request, response: Response, model: Model) =
        collection()
            .delete(request.id())
            ?.also { response.status(204) }
            ?: notFound(request)

}
