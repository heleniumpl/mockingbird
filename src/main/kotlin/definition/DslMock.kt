package pl.helenium.mockingbird.definition

import pl.helenium.mockingbird.model.Context
import pl.helenium.mockingbird.model.HttpMethod.DELETE
import pl.helenium.mockingbird.model.HttpMethod.GET
import pl.helenium.mockingbird.model.HttpMethod.POST
import pl.helenium.mockingbird.model.HttpMethod.PUT
import pl.helenium.mockingbird.model.MetaModel
import pl.helenium.mockingbird.model.MetaModel.MetaModelDsl
import pl.helenium.mockingbird.server.Route

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

    fun routes(dsl: RoutesDsl.() -> Unit) = RoutesDsl(context).dsl()

}

class RoutesDsl(private val context: Context) {

    fun post(uri: String, route: Route) = context.defineRoute(POST, uri, route)

    fun get(uri: String, route: Route) = context.defineRoute(GET, uri, route)

    fun put(uri: String, route: Route) = context.defineRoute(PUT, uri, route)

    fun delete(uri: String, route: Route) = context.defineRoute(DELETE, uri, route)

}
