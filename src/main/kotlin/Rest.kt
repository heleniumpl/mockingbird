package pl.helenium.mockingbird

import com.fasterxml.jackson.databind.ObjectMapper
import spark.Request
import spark.Response
import spark.Route

class Rest<M, R>(
    private val context: Context,
    private val metaModel: MetaModel,
    private val requestParser: (String) -> Model = ::jsonRequestParser,
    private val unwrapper: (Model) -> Model = ::identity,
    private val restHandler: RestHandler<M>,
    private val wrapper: (M) -> R,
    private val requestWriter: (R) -> Any
) : Route {

    override fun handle(request: Request, response: Response): Any {
        val inModel = unwrapper(requestParser(request.body()))
        val outModel = restHandler.handle(request, response, context, metaModel, inModel)
        return requestWriter(wrapper(outModel))
    }

}

interface RestHandler<M> {

    fun handle(request: Request, response: Response, context: Context, metaModel: MetaModel, model: Model): M

}

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

fun jsonRequestParser(body: String) = Model(ObjectMapper().readMap(body))

fun jsonRequestWriter(model: Model) = ObjectMapper().writeValueAsString(model.asMap())!!

fun <T> identity(arg: T) = arg
