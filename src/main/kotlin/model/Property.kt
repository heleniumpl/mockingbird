package pl.helenium.mockingbird.model

class Property(val name: String) {

    var id = false

    var required = false

    var generate: () -> Any? = { null }

    val validators = mutableListOf<Validator>()

    fun dsl() = PropertyDsl()

    inner class PropertyDsl {

        fun id() = apply { id = true }

        fun required(required: Boolean = true) = apply {
            this@Property.required = required
            if(required) validators += RequiredPropertyValidator(this@Property)
        }

        fun generator(generator: () -> Any?) = apply { this@Property.generate = generator }

    }

}

class PropertiesDsl(private val properties: MutableList<Property>) {

    fun id(name: String = "id") = property(name).id()

    fun property(name: String) = Property(name)
        .also { properties += it }
        .dsl()

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
