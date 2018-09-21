package pl.helenium.mockingbird.definition.rest

import pl.helenium.mockingbird.Context
import pl.helenium.mockingbird.definition.identity
import pl.helenium.mockingbird.json.jsonRequestParser
import pl.helenium.mockingbird.model.MetaModel
import pl.helenium.mockingbird.model.Model
import spark.Request
import spark.Response
import spark.Route

// FIXME this should have more of DSL form
class RestRoute<M, R>(
    private val context: Context,
    private val metaModel: MetaModel,
    private val requestParser: (String) -> Model = ::jsonRequestParser,
    private val unwrapper: (Model) -> Model = ::identity,
    private val restHandler: RestHandler<M>,
    private val wrapper: (M) -> R,
    private val requestWriter: (R) -> Any?
) : Route {

    override fun handle(request: Request, response: Response): Any? {
        val inModel = unwrapper(requestParser(request.body()))
        val outModel = restHandler.handle(request, response, context, metaModel, inModel)
        return requestWriter(wrapper(outModel))
    }

}

interface RestHandler<M> {

    fun handle(
        request: Request,
        response: Response,
        context: Context,
        metaModel: MetaModel,
        model: Model
    ): M

}
