package pl.helenium.mockingbird.server.spark

import pl.helenium.mockingbird.server.ResponseAdapter
import spark.Response

class SparkResponseAdapter(private val response: Response) : ResponseAdapter {

    override fun status(status: Int) = response.status(status)

}
