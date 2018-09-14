package pl.helenium.mockingbird

import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging
import spark.Request
import spark.Response
import spark.Route

private val logger = KotlinLogging.logger {}

class GenericRoute(private val context: Context) : Route {

    override fun handle(request: Request, response: Response): Any {
        val body = request.body()
        logger.info { "Received request: ${request.requestMethod()} ${request.uri()} <- $body" }

        val bodyAsObject: Map<String, Any?> = ObjectMapper().readMap(body)
        val model = Model(bodyAsObject)
        val modelUnpacked = model.embeddedModel("data")
        val created = context
            .collection("contact")
            .create(modelUnpacked)
        val packed = Model(mapOf("data" to created.asMap()))

        return ObjectMapper().writeValueAsString(packed.asMap())
    }

}
