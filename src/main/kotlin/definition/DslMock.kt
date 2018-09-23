package pl.helenium.mockingbird.definition

import pl.helenium.mockingbird.model.Context
import pl.helenium.mockingbird.model.HttpMethod
import pl.helenium.mockingbird.model.HttpMethod.DELETE
import pl.helenium.mockingbird.model.HttpMethod.GET
import pl.helenium.mockingbird.model.HttpMethod.POST
import pl.helenium.mockingbird.model.HttpMethod.PUT
import pl.helenium.mockingbird.model.MetaModel
import pl.helenium.mockingbird.model.MetaModel.MetaModelDsl
import pl.helenium.mockingbird.server.RouteAdapter

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

    // FIXME all DSL helper classes should be in one place
    inner class RoutesDsl {

        fun get(dsl: MethodDsl.() -> Unit) = MethodDsl(GET)(dsl)

        fun post(dsl: MethodDsl.() -> Unit) = MethodDsl(POST)(dsl)

        fun put(dsl: MethodDsl.() -> Unit) = MethodDsl(PUT)(dsl)

        fun delete(dsl: MethodDsl.() -> Unit) = MethodDsl(DELETE)(dsl)

        inner class MethodDsl(private val method: HttpMethod) {

            lateinit var uri: String

            lateinit var handler: RouteAdapter

            operator fun invoke(dsl: MethodDsl.() -> Unit) {
                dsl()
                context.defineRoute(method, uri, handler)
            }

        }

    }

}
