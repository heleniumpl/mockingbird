package pl.helenium.mockingbird

import spark.Service

interface Context {

    val server: Service

    fun collection(metaModel: String): ModelCollection

}
