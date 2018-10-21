package pl.helenium.mockingbird.json

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature.USE_LONG_FOR_INTS
import com.fasterxml.jackson.databind.ObjectMapper
import pl.helenium.mockingbird.definition.rest.ResponseWriter
import pl.helenium.mockingbird.model.Model
import pl.helenium.mockingbird.server.Response

val defaultObjectMapper: ObjectMapper = ObjectMapper()
    .enable(USE_LONG_FOR_INTS)

fun ObjectMapper.readStringKeyMap(payload: String): Map<String, Any?> =
    readValue<Map<String, Any?>>(payload, object : TypeReference<Map<String, Any?>>() {})

fun jsonRequestParser(body: String) = Model(defaultObjectMapper.readStringKeyMap(body))

object JsonResponseWriter : ResponseWriter<Model> {

    override fun write(model: Model, response: Response): Any? {
        response.contentType("application/json")
        return defaultObjectMapper.writeValueAsString(model.asMap())
    }

}
