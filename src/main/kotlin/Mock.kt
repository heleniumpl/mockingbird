package pl.helenium.mockingbird

import pl.helenium.mockingbird.MetaModel.MetaModelDsl
import spark.Route

open class DslMock(private val context: Context, builder: DslMock.() -> Unit) {

    lateinit var metaModel: MetaModel
        private set

    init {
        builder()
    }

    fun metaModel(name: String, dsl: MetaModelDsl.() -> Unit) {
        this.metaModel = MetaModel(name).apply { dsl().apply(dsl) }
    }

    fun get(uri: String, route: Route) {
        context.server.get(uri, route)
    }

    fun post(uri: String, route: Route) {
        context.server.post(uri, route)
    }

}
