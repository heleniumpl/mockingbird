package pl.helenium.mockingbird

import spark.Service

interface Context {

    val server: Service

    val port: Int

    fun registerMetaModel(metaModel: MetaModel)

    fun metaModel(name: String): MetaModel

    fun collection(metaModel: MetaModel): ModelCollection

}
