package pl.helenium.mockingbird

import pl.helenium.mockingbird.model.MetaModel
import pl.helenium.mockingbird.model.ModelCollection
import spark.Service

interface Context {

    val server: Service

    val port: Int

    fun registerMetaModel(metaModel: MetaModel)

    fun metaModel(name: String): MetaModel

    fun collection(metaModel: MetaModel): ModelCollection

}
