package pl.helenium.mockingbird.exception

import pl.helenium.mockingbird.model.MetaModel
import pl.helenium.mockingbird.model.ModelError
import pl.helenium.mockingbird.server.Request
import java.io.PrintWriter
import java.io.StringWriter

abstract class AbstractHttpException(val status: Int, message: String) : RuntimeException(message)

class Unauthorized : AbstractHttpException(401, "You are unauthorized!")

class NotFoundException(message: String) : AbstractHttpException(404, message)

class ModelException(errors: List<ModelError>): AbstractHttpException(422, errors.joinToString(separator = "\n"))

fun unauthorized(): Nothing = throw Unauthorized()

fun notFound(metaModel: MetaModel, request: Request): Nothing =
    throw NotFoundException("Model ${metaModel.name}#${request.param("id")} not found!")

fun modelErrors(errors: List<ModelError>) {
    if (errors.isEmpty()) {
        return
    }

    throw ModelException(errors)
}

fun Throwable.getStackTraceAsString() = StringWriter()
    .apply { printStackTrace(PrintWriter(this)) }
    .toString()
