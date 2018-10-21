package pl.helenium.mockingbird.server.spark

import pl.helenium.mockingbird.server.Response
import spark.Response as SparkResponse

class SparkResponse(private val response: SparkResponse) : Response {

    override fun status(status: Int) = response.status(status)

    override fun header(name: String, value: String) = response.header(name, value)

}
