package pl.helenium.mockingbird.test.mock

import pl.helenium.mockingbird.definition.DslMock
import pl.helenium.mockingbird.exception.unauthorized
import pl.helenium.mockingbird.model.Authorization
import pl.helenium.mockingbird.model.Context
import pl.helenium.mockingbird.server.Request

class HelloWorldMock(context: Context) : DslMock(context, {

    routes {
        get("/hello_world") { _, _ ->
            "Hello World!"
        }

        get("/hello_world/exception") { _, _ ->
            throw RuntimeException("Hello World Exception!")
        }

        get("/hello_world/authorized") { request, _ ->
            val actor = context.authorize(request) ?: unauthorized()
            "Hello ${actor.name}!"
        }
    }

})

private fun Context.authorize(request: Request) = actors
    .scope("hello world")
    .authorize(request.authorization())

private fun Request.authorization() = header("Authorization")
    ?.takeIf { it.startsWith("Bearer ") }
    ?.removePrefix("Bearer ")
    ?.let(::Authorization)
