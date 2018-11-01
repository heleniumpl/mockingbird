package pl.helenium.mockingbird.model

import pl.helenium.mockingbird.model.Property.PropertyDsl

class Property(val name: String, buildBlock: PropertyDsl.() -> Unit = {}) {

    var id = false

    var required = false

    var sortable = true

    var type: Type = AnyType

    val validators = mutableListOf<Validator>()

    init {
        PropertyDsl().buildBlock()
    }

    inner class PropertyDsl {

        fun id() = apply {
            id = true
            sortable()
        }

        fun required(required: Boolean = true) = apply {
            this@Property.required = required
            if (required) validators += RequiredPropertyValidator(this@Property)
        }

        fun sortable(sortable: Boolean = true) = apply { this@Property.sortable = sortable }

        fun type(type: Type) = apply {
            this@Property.type = type
            validators += PropertyTypeValidator(this@Property)
        }

        // TODO relatesTo

        // TODO default

        // TODO generators --^

    }

}

class PropertiesDsl(private val properties: MutableList<Property>) {

    fun id(name: String = "id", buildBlock: PropertyDsl.() -> Unit = {}) = property(name) {
        id()
        buildBlock()
    }

    fun property(name: String, buildBlock: PropertyDsl.() -> Unit) = Property(name, buildBlock)
        .also { properties += it }
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

class PropertyTypeValidator(private val property: Property) : Validator {

    override fun validate(
        context: Context,
        metaModel: MetaModel,
        actor: Actor?,
        model: Model,
        errors: MutableList<ModelError>
    ) {
        property
            .type
            .validate(property.name, model.getProperty(property.name) ?: return, errors)
    }

}
