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
            allValidators().forEach {
                it.validate(context, this@MetaModel, actor, model, this)
            }
        }.also { modelErrors(it) }
    }

    fun dsl() = MetaModelDsl()

    private fun allValidators() = sequence {
        yieldAll(validators)
        yieldAll(
            properties
                .asSequence()
                .flatMap { it.validators.asSequence() }
        )
    }

    inner class MetaModelDsl {

        fun properties(buildBlock: PropertiesDsl.() -> Unit) = PropertiesDsl(properties).buildBlock()

        fun lifecycleHandlers(buildBlock: LifecycleHandlersDsl.() -> Unit) = LifecycleHandlersDsl().buildBlock()

        fun validators(buildBlock: ValidatorsDsl.() -> Unit) = ValidatorsDsl().buildBlock()

        inner class LifecycleHandlersDsl {

            operator fun LifecycleHandler.unaryPlus() {
                lifecycleHandlers += this
            }

        }

        inner class ValidatorsDsl {

            operator fun Validator.unaryPlus() {
                validators += this
            }

        }

    }

}
