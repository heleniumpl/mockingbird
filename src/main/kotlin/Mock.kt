package pl.helenium.mockingbird

import spark.Route
import spark.Service

interface Mock {

    fun register(): (Mockingbird) -> Unit

}

open class DslMock(private val init: DslMock.() -> Unit) : Mock {

    private val routes = mutableListOf<Service.() -> Unit>()

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

    override fun register(): Context.() -> Unit = {
        this@DslMock.init()
        routes.forEach { route ->
            server.route()
        }
    }

}
