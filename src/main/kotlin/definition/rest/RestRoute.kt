package pl.helenium.mockingbird.definition.rest

import pl.helenium.mockingbird.definition.identity
import pl.helenium.mockingbird.exception.notFound
import pl.helenium.mockingbird.json.jsonRequestParser
import pl.helenium.mockingbird.model.Context
import pl.helenium.mockingbird.model.MetaModel
import pl.helenium.mockingbird.model.Model
import pl.helenium.mockingbird.server.RequestAdapter
import pl.helenium.mockingbird.server.ResponseAdapter
import pl.helenium.mockingbird.server.RouteAdapter

// FIXME this should have more of DSL form
class RestRoute<M, R>(
    private val requestParser: (String) -> Model = ::jsonRequestParser,
    private val unwrapper: (Model) -> Model = ::identity,
    private val restHandler: RestHandler<M>,
    private val wrapper: (M) -> R,
    private val requestWriter: (R) -> Any?
) : RouteAdapter {

    override fun invoke(request: RequestAdapter, response: ResponseAdapter): Any? {
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

    abstract fun handle(request: RequestAdapter, response: ResponseAdapter, model: Model): M

    protected fun collection() = context
        .modelCollections
        .byMetaModel(metaModel)

    protected fun RequestAdapter.id() = this.param("id") ?: throw IllegalStateException()

    protected fun notFound(request: RequestAdapter): Nothing = notFound(metaModel, request)

}
