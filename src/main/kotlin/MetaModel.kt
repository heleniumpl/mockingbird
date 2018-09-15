package pl.helenium.mockingbird

import java.util.concurrent.atomic.AtomicLong

data class MetaModel(val name: String) {

    private val properties = mutableListOf<Property>()

    fun id() = properties.find { it.id } ?: throw IllegalStateException("MetaModel $name does not have ID!")

    fun dsl() = MetaModelDsl()

    inner class MetaModelDsl {

        fun properties(dsl: PropertiesDsl.() -> Unit) = PropertiesDsl().dsl()

        inner class PropertiesDsl {

            fun id(name: String) = Property(name)
                .id()
                .also { properties.add(it) }

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

object LongGenerator: () -> Any? {

    private val sequence = AtomicLong(1)

    override fun invoke(): Any? = sequence.getAndIncrement()

}
