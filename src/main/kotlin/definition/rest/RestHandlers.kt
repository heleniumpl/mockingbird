package pl.helenium.mockingbird.definition.rest

import pl.helenium.mockingbird.Context
import pl.helenium.mockingbird.model.MetaModel
import pl.helenium.mockingbird.model.Model
import spark.Request
import spark.Response

class RestCreateHandler(context: Context, metaModel: MetaModel) : RestHandler<Model>(context, metaModel) {

    override fun handle(request: Request, response: Response, model: Model) =
        context
            .collection(metaModel)
            .create(model)

}

// FIXME add support for filtering
// FIXME add support for sorting
// FIXME add support for paging
class RestListHandler(context: Context, metaModel: MetaModel) : RestHandler<Collection<Model>>(context, metaModel) {

    override fun handle(request: Request, response: Response, model: Model) =
        context
            .collection(metaModel)
            .list()

}

class RestGetHandler(context: Context, metaModel: MetaModel) : RestHandler<Model>(context, metaModel) {

    override fun handle(request: Request, response: Response, model: Model) =
        context
            .collection(metaModel)
            .get(request.id())
            ?: notFound(request)

}

class RestUpdateHandler(context: Context, metaModel: MetaModel) : RestHandler<Model>(context, metaModel) {

    override fun handle(request: Request, response: Response, model: Model) =
        context
            .collection(metaModel)
            .update(request.id(), model, RestUpdater)
            ?: notFound(request)
}

class RestDeleteHandler(context: Context, metaModel: MetaModel) : RestHandler<Model>(context, metaModel) {

    override fun handle(request: Request, response: Response, model: Model) =
        context
            .collection(metaModel)
            .delete(request.id())
            ?.also { response.status(204) }
            ?: notFound(request)

}
