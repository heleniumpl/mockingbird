package pl.helenium.mockingbird

import pl.helenium.mockingbird.MetaModel.MetaModelDsl
import spark.Route
import spark.Service

open class DslMock(private val context: Context, builder: DslMock.() -> Unit) {

    lateinit var metaModel: MetaModel
        private set

    init {
        builder()
    }

    fun metaModel(name: String, dsl: MetaModelDsl.() -> Unit) {
        this.metaModel = MetaModel(name)
            .apply { dsl().apply(dsl) }
            .also(context::registerMetaModel)
    }

    fun routes(dsl: RoutesDsl.() -> Unit) = RoutesDsl().dsl()

    inner class RoutesDsl {

        fun get(dsl: MethodDsl.() -> Unit) = MethodDsl(Service::get)(dsl)

        fun post(dsl: MethodDsl.() -> Unit) = MethodDsl(Service::post)(dsl)

        fun delete(dsl: MethodDsl.() -> Unit) = MethodDsl(Service::delete)(dsl)

        inner class MethodDsl(val addRoute: Service.(String, Route) -> Unit) {

            lateinit var uri: String

            lateinit var handler: Route

            operator fun invoke(dsl: MethodDsl.() -> Unit) {
                dsl()
                context.server.addRoute(uri, handler)
            }

        }

    }

}
