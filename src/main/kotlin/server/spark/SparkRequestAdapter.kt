package pl.helenium.mockingbird.server.spark

import pl.helenium.mockingbird.server.RequestAdapter
import spark.Request

class SparkRequestAdapter(private val request: Request) : RequestAdapter {

    override fun param(name: String): String? = request.params(name)

    override fun header(name: String): String? = request.headers(name)

    override fun body(): String = request.body()

}
