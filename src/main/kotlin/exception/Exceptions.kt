package pl.helenium.mockingbird.exception

import pl.helenium.mockingbird.model.MetaModel
import pl.helenium.mockingbird.server.RequestAdapter
import java.io.PrintWriter
import java.io.StringWriter

abstract class AbstractHttpException(val status: Int, message: String) : RuntimeException(message)

class Unauthorized : AbstractHttpException(401, "You are unauthorized!")

class NotFoundException(message: String) : AbstractHttpException(404, message)

fun unauthorized(): Nothing = throw Unauthorized()

fun notFound(metaModel: MetaModel, request: RequestAdapter): Nothing =
    throw NotFoundException("Model ${metaModel.name}#${request.param("id")} not found!")

fun Throwable.getStackTraceAsString() = StringWriter()
    .apply { printStackTrace(PrintWriter(this)) }
    .toString()
