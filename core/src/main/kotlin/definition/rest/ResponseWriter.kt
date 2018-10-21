package pl.helenium.mockingbird.definition.rest

import pl.helenium.mockingbird.json.defaultObjectMapper
import pl.helenium.mockingbird.model.Model
import pl.helenium.mockingbird.server.Response

interface ResponseWriter<T> {

    fun write(model: T, response: Response): Any?

}

object EmptyResponseWriter : ResponseWriter<Model> {

    override fun write(model: Model, response: Response) = ""

}

object JsonResponseWriter : ResponseWriter<Model> {

    override fun write(model: Model, response: Response): Any? {
        response.contentType("application/json")
        return defaultObjectMapper.writeValueAsString(model.asMap())
    }

}
