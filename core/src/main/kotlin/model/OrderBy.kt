package pl.helenium.mockingbird.model

import pl.helenium.mockingbird.model.Direction.ASC
import pl.helenium.mockingbird.model.Nulls.LAST

class OrderBy(vararg orders: Comparator<Model>) : Comparator<Model> {

    private val comparator = orders
        .reduce { acc, v-> acc.thenComparing(v) }

    override fun compare(o1: Model, o2: Model) = comparator.compare(o1, o2)

}

// FIXME needs spec
class Order(
    private val property: Property,
    private val direction: Direction = ASC,
    private val nulls: Nulls = LAST
) : Comparator<Model> {

    init {
        if(!property.sortable) throw IllegalArgumentException("Property `${property.name}` is not sortable!")
    }

    override fun compare(o1: Model, o2: Model): Int {
        val v1 = o1.getProperty<Any?>(property.name)
        val v2 = o2.getProperty<Any?>(property.name)
        return when {
            v1 === v2 -> 0
            v1 == null -> -nulls.modifier
            v2 == null -> nulls.modifier
            else -> property
                .type
                .comparator()
                .compare(v1, v2) * direction.modifier
        }
    }

    fun toOrderBy() = OrderBy(this)

}

enum class Direction(val modifier: Int) {
    ASC(1),
    DESC(-1),
    ;
}

enum class Nulls(val modifier: Int) {
    FIRST(1),
    LAST(-1),
    ;
}
