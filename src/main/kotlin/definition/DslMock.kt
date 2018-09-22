package pl.helenium.mockingbird.definition

import pl.helenium.mockingbird.Context
import pl.helenium.mockingbird.model.MetaModel
import pl.helenium.mockingbird.model.MetaModel.MetaModelDsl
import spark.Route
import spark.Service

open class DslMock(private val context: Context, builder: DslMock.() -> Unit) {

    lateinit var metaModel: MetaModel
        private set

    init {
        builder()
    }

    // FIXME consider if this is the proper way of registering meta model
    fun metaModel(name: String, dsl: MetaModelDsl.() -> Unit) {
        this.metaModel = MetaModel(name)
            .apply { dsl().apply(dsl) }
            .also { context.metaModels.register(it) }
    }

    fun routes(dsl: RoutesDsl.() -> Unit) = RoutesDsl().dsl()

    // all DSL helper classes should be in one place
    inner class RoutesDsl {

        fun get(dsl: MethodDsl.() -> Unit) = MethodDsl(Service::get)(dsl)

        fun post(dsl: MethodDsl.() -> Unit) = MethodDsl(Service::post)(dsl)

        fun put(dsl: MethodDsl.() -> Unit) = MethodDsl(Service::put)(dsl)

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
