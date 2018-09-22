package pl.helenium.mockingbird.definition.rest

import pl.helenium.mockingbird.definition.identity
import pl.helenium.mockingbird.exception.notFound
import pl.helenium.mockingbird.json.jsonRequestParser
import pl.helenium.mockingbird.model.Context
import pl.helenium.mockingbird.model.MetaModel
import pl.helenium.mockingbird.model.Model
import spark.Request
import spark.Response
import spark.Route

// FIXME this should have more of DSL form
class RestRoute<M, R>(
    private val requestParser: (String) -> Model = ::jsonRequestParser,
    private val unwrapper: (Model) -> Model = ::identity,
    private val restHandler: RestHandler<M>,
    private val wrapper: (M) -> R,
    private val requestWriter: (R) -> Any?
) : Route {

    override fun handle(request: Request, response: Response): Any? {
        val inModel = unwrapper(requestParser(request.body()))
        val outModel = restHandler.handle(request, response, inModel)
        return requestWriter(wrapper(outModel))
    }

}

// FIXME this is not REST-specific
abstract class RestHandler<M>(
    protected val context: Context,
    protected val metaModel: MetaModel
) {

    abstract fun handle(request: Request, response: Response, model: Model): M

    protected fun collection() = context
        .modelCollections
        .byMetaModel(metaModel)

    protected fun Request.id() = this.params("id") ?: throw IllegalStateException()

    protected fun notFound(request: Request): Nothing = notFound(metaModel, request)

}
