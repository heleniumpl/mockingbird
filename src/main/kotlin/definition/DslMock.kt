package pl.helenium.mockingbird.definition

import pl.helenium.mockingbird.model.Actor
import pl.helenium.mockingbird.model.Context
import pl.helenium.mockingbird.model.Handler
import pl.helenium.mockingbird.model.MetaModel
import pl.helenium.mockingbird.model.MetaModel.MetaModelDsl
import pl.helenium.mockingbird.server.HttpMethod
import pl.helenium.mockingbird.server.HttpMethod.DELETE
import pl.helenium.mockingbird.server.HttpMethod.GET
import pl.helenium.mockingbird.server.HttpMethod.POST
import pl.helenium.mockingbird.server.HttpMethod.PUT
import pl.helenium.mockingbird.server.Request

typealias Authenticator = (Request) -> Actor?

open class DslMock(private val context: Context, builder: DslMock.() -> Unit) {

    private var metaModel: MetaModel? = null

    private var authenticator: Authenticator? = null

    init {
        builder()
    }

    // FIXME consider if this is the proper way of registering meta model
    fun metaModel(name: String, buildBlock: MetaModelDsl.() -> Unit) {
        this.metaModel = MetaModel(name)
            .apply { dsl().buildBlock() }
            .also { context.metaModels.register(it) }
    }

    fun authenticator(authenticator: Authenticator) {
        this.authenticator = authenticator
    }

    fun handlers(buildBlock: HandlersDsl.() -> Unit) = HandlersDsl().buildBlock()

    fun metaModel(): MetaModel = metaModel
        ?: throw IllegalStateException("MetaModel has to be defined before it is accessed!")

    inner class HandlersDsl {

        fun post(uri: String, handler: Handler) = register(POST, uri, handler)

        fun get(uri: String, handler: Handler) = register(GET, uri, handler)

        fun put(uri: String, handler: Handler) = register(PUT, uri, handler)

        fun delete(uri: String, handler: Handler) = register(DELETE, uri, handler)

        private fun register(method: HttpMethod, uri: String, handler: Handler) = context
            .handlers
            .register(method, uri, handler, authenticator)

    }

}
