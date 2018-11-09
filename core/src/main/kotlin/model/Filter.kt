package pl.helenium.mockingbird.model

import pl.helenium.mockingbird.server.Request
import java.util.*

typealias Filter = (Model) -> Boolean

val noFilter: Filter = { true }

data class PropertyFilter(private val property: Property, private val value: Any) : Filter {

    init {
        if (!property.filterable) throw IllegalArgumentException("Property `${property.name}` is not filterable!")
    }

    override fun invoke(model: Model) = model.getProperty<Any?>(property.name) == value

}

data class AndFilter(private val filters: List<Filter>) : Filter {

    override fun invoke(model: Model) = filters.all { it(model) }

}

fun extractPropertyFilter(metaModel: MetaModel, request: Request): Filter = AndFilter(
    metaModel
        .properties()
        .filter(Property::filterable)
        .associateWith { request.queryParam(it.name) }
        .filterValues(Objects::nonNull)
        .mapValues { it.value as Any }
        .map { (k, v) -> PropertyFilter(k, v) }
)
