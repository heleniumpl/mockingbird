package pl.helenium.mockingbird.test.mock

import pl.helenium.mockingbird.Context
import pl.helenium.mockingbird.definition.DslMock
import pl.helenium.mockingbird.exception.unauthorized
import pl.helenium.mockingbird.model.Authorization
import spark.Request
import spark.Route

class HelloWorldMock(context: Context) : DslMock(context, {

    routes {
        get {
            uri = "/hello_world"
            handler = Route { _, _ ->
                "Hello World!"
            }
        }

        get {
            uri = "/hello_world/exception"
            handler = Route { _, _ ->
                throw RuntimeException("Hello World Exception!")
            }
        }

        get {
            uri = "/hello_world/authorized"
            handler = Route { request, _ ->
                val actor = context.authorize(request) ?: unauthorized()
                "Hello ${actor.name}!"
            }
        }
    }

})

private fun Context.authorize(request: Request) = actors
    .scope("hello world")
    .authorize(request.authorization())

private fun Request.authorization() = headers("Authorization")
    ?.takeIf { it.startsWith("Bearer ") }
    ?.removePrefix("Bearer ")
    ?.let(::Authorization)
