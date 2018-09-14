package pl.helenium.mockingbird

import com.fasterxml.jackson.databind.ObjectMapper
import spark.Request
import spark.Response
import spark.Route

class Create(
    private val context: Context,
    private val metaModel: MetaModel,
    private val requestParser: (String) -> Model = ::jsonRequestParser,
    private val unwrapper: (Model) -> Model = ::identity,
    private val wrapper: (Model) -> Model = ::identity,
    private val requestWriter: (Model) -> Any = ::jsonRequestWriter
) : Route {

    override fun handle(request: Request, response: Response): Any {
        val model = unwrapper(requestParser(request.body()))
        val created = context
            .collection(metaModel)
            .create(model)
        return requestWriter(wrapper(created))
    }

}

fun jsonRequestParser(body: String) = Model(ObjectMapper().readMap(body))

fun jsonRequestWriter(model: Model) = ObjectMapper().writeValueAsString(model.asMap())!!

fun <T> identity(arg: T) = arg
