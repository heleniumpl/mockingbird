package pl.helenium.mockingbird

import spark.Route
import spark.Service

open class DslMock(context: Context, builder: DslMock.() -> Unit) {

    private val routes = mutableListOf<Service.() -> Unit>()

    init {
        builder()
        routes.forEach { route ->
            context.server.route()
        }
    }

    fun get(uri: String, route: () -> Route) {
        routes += {
            get(uri, route())
        }
    }

    fun post(uri: String, route: () -> Route) {
        routes += {
            post(uri, route())
        }
    }

}
