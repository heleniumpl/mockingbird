package pl.helenium.mockingbird.definition.rest

import pl.helenium.mockingbird.Context
import pl.helenium.mockingbird.exception.NotFoundException
import pl.helenium.mockingbird.model.MetaModel
import pl.helenium.mockingbird.model.Model
import spark.Request
import spark.Response

class RestCreateHandler : RestHandler<Model> {

    override fun handle(
        request: Request,
        response: Response,
        context: Context,
        metaModel: MetaModel,
        model: Model
    ): Model =
        context
            .collection(metaModel)
            .create(model)

}

// FIXME add support for filtering
// FIXME add support for sorting
// FIXME add support for paging
class RestListHandler : RestHandler<Collection<Model>> {

    override fun handle(
        request: Request,
        response: Response,
        context: Context,
        metaModel: MetaModel,
        model: Model
    ): Collection<Model> =
        context
            .collection(metaModel)
            .list()

}

class RestGetHandler : RestHandler<Model> {

    override fun handle(
        request: Request,
        response: Response,
        context: Context,
        metaModel: MetaModel,
        model: Model
    ): Model =
        context
            .collection(metaModel)
            .get(request.params("id"))
            ?: throw NotFoundException()

}

class RestUpdateHandler : RestHandler<Model> {

    override fun handle(
        request: Request,
        response: Response,
        context: Context,
        metaModel: MetaModel,
        model: Model
    ): Model =
        context
            .collection(metaModel)
            .update(request.params("id"), model, RestUpdater)
}

class RestDeleteHandler : RestHandler<Model> {

    override fun handle(
        request: Request,
        response: Response,
        context: Context,
        metaModel: MetaModel,
        model: Model
    ) = context
        .collection(metaModel)
        .delete(request.params("id"))
        ?.also { response.status(204) }
        ?: throw NotFoundException()

}
