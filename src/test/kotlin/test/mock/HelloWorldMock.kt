package pl.helenium.mockingbird.test.mock

import pl.helenium.mockingbird.definition.DslMock
import pl.helenium.mockingbird.exception.unauthorized
import pl.helenium.mockingbird.model.Authorization
import pl.helenium.mockingbird.model.Context
import pl.helenium.mockingbird.server.Request

class HelloWorldMock(context: Context) : DslMock(context, {

    routes {
        get {
            uri = "/hello_world"
            handler = { _, _ ->
                "Hello World!"
            }
        }

        get {
            uri = "/hello_world/exception"
            handler = { _, _ ->
                throw RuntimeException("Hello World Exception!")
            }
        }

        get {
            uri = "/hello_world/authorized"
            handler = { request, _ ->
                val actor = context.authorize(request) ?: unauthorized()
                "Hello ${actor.name}!"
            }
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
