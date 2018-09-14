package pl.helenium.mockingbird

import com.fasterxml.jackson.databind.ObjectMapper
import spark.Request
import spark.Response
import spark.Route

class GenericRoute(
    private val context: Context,
    private val metaModel: MetaModel
) : Route {

    override fun handle(request: Request, response: Response): Any {
        val body = request.body()
        val bodyAsObject: Map<String, Any?> = ObjectMapper().readMap(body)
        val model = Model(bodyAsObject)
        val modelUnpacked = model.embeddedModel("data")
        val created = context
            .collection(metaModel)
            .create(modelUnpacked)
        val packed = Model(mapOf("data" to created.asMap()))

        return ObjectMapper().writeValueAsString(packed.asMap())
    }

}
