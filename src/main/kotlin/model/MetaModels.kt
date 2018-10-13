package pl.helenium.mockingbird.model

import pl.helenium.mockingbird.exception.modelErrors

class MetaModels {

    private val metaModels = mutableMapOf<String, MetaModel>()

    fun register(metaModel: MetaModel) {
        metaModels[metaModel.name] = metaModel
    }

    fun byName(name: String) =
        metaModels[name] ?: throw IllegalArgumentException("No MetaModel could be found for name $name!")

}

data class MetaModel(val name: String) {

    private val properties = mutableListOf<Property>()

    private val lifecycleHandlers = mutableListOf<LifecycleHandler>()

    private val validators = mutableListOf<Validator>()

    fun id() = properties.find { it.id } ?: throw IllegalStateException("MetaModel $name does not have ID!")

    fun lifecycleHandlers(): List<LifecycleHandler> = lifecycleHandlers

    fun validate(context: Context, actor: Actor?, model: Model) {
        mutableListOf<ModelError>().apply {
            validators.forEach {
                it.validate(context, this@MetaModel, actor, model, this)
            }
        }.also { modelErrors(it) }
    }

    fun dsl() = MetaModelDsl()

    inner class MetaModelDsl {

        fun properties(dsl: PropertiesDsl.() -> Unit) = PropertiesDsl().dsl()

        fun lifecycleHandlers(dsl: LifecycleHandlersDsl.() -> Unit) = LifecycleHandlersDsl().dsl()

        fun validators(dsl: ValidatorsDsl.() -> Unit) = ValidatorsDsl().dsl()

        inner class PropertiesDsl {

            fun id(name: String = "id") = Property(name)
                .id()
                .also { properties.add(it) }

        }

        inner class LifecycleHandlersDsl {

            operator fun LifecycleHandler.unaryPlus() { lifecycleHandlers += this }

        }

        inner class ValidatorsDsl {

            operator fun Validator.unaryPlus() { validators += this }

        }

    }

}

class Property(val name: String) {

    var id = false
        private set

    var generate: () -> Any? = { null }
        private set

    fun id() = apply { id = true }

    fun generator(generator: () -> Any?) = apply { this.generate = generator }

}
