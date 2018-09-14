package pl.helenium.mockingbird

data class MetaModel(val name: String) {

    private val properties = mutableListOf<Property>()

    fun id() = properties.find { it.id } ?: throw IllegalStateException("MetaModel $name does not have ID!")

    fun dsl() = MetaModelDsl()

    inner class MetaModelDsl {

        fun properties(dsl: PropertiesDsl.() -> Unit) = PropertiesDsl().dsl()

        inner class PropertiesDsl {

            fun id(name: String) = properties.add(Property(name).id())

        }

    }

}

class Property(val name: String) {

    var id = false
        private set

    fun id() = apply { id = true }

}
