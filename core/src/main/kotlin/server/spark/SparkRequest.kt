package pl.helenium.mockingbird.server.spark

import pl.helenium.mockingbird.server.Request
import spark.Request as SparkRequest

class SparkRequest(private val request: SparkRequest) : Request {

    override fun param(name: String): String? = request.params(name)

    override fun header(name: String): String? = request.headers(name)

    override fun body(): String = request.body()

}
