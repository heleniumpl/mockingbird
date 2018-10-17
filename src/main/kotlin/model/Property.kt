package pl.helenium.mockingbird.model

class Property(val name: String) {

    var id = false
        private set

    var required = false
        private set

    var generate: () -> Any? = { null }
        private set

    private val validators = mutableListOf<Validator>()

    fun id() = apply { id = true }

    fun required(required: Boolean = true) = apply {
        this.required = required
        if(required) validators += RequiredPropertyValidator(this)
    }

    fun generator(generator: () -> Any?) = apply { this.generate = generator }

    fun validators(): List<Validator> = validators

}

class PropertiesDsl(private val properties: MutableList<Property>) {

    fun id(name: String = "id") = property(name).id()

    fun property(name: String) = Property(name).also { properties += it }

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
