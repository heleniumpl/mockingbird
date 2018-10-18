package pl.helenium.mockingbird.model

import pl.helenium.mockingbird.model.Property.PropertyDsl

class Property(val name: String) {

    var id = false

    var required = false

    var generate: () -> Any? = { null }

    val validators = mutableListOf<Validator>()

    inner class PropertyDsl {

        fun id() = apply { id = true }

        fun required(required: Boolean = true) = apply {
            this@Property.required = required
            if (required) validators += RequiredPropertyValidator(this@Property)
        }

        fun generator(generator: () -> Any?) = apply { this@Property.generate = generator }

    }

}

class PropertiesDsl(private val properties: MutableList<Property>) {

    fun id(name: String = "id", buildBlock: PropertyDsl.() -> Unit = {}) = property(name) {
        id()
        buildBlock()
    }

    fun property(name: String, buildBlock: PropertyDsl.() -> Unit) = Property(name)
        .also { properties += it }
        .PropertyDsl()
        .buildBlock()
}

class RequiredPropertyValidator(private val property: Property) : Validator {

    override fun validate(
        context: Context,
        metaModel: MetaModel,
        actor: Actor?,
        model: Model,
        errors: MutableList<ModelError>
    ) {
        if (!property.required || model.getProperty<Any?>(property.name) != null) return

        errors += "Property '${property.name}' is required but was not given!"
    }

}
