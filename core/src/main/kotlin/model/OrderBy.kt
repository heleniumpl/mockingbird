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
    private val property: String,
    private val direction: Direction = ASC,
    private val nulls: Nulls = LAST
) : Comparator<Model> {

    override fun compare(o1: Model, o2: Model): Int {
        val o1propValue = o1.getProperty<Comparable<Any>?>(property)
        val o2propValue = o2.getProperty<Comparable<Any>?>(property)
        return when {
            o1propValue === o2propValue -> 0
            o1propValue == null -> -nulls.modifier
            o2propValue == null -> nulls.modifier
            else -> o1propValue.compareTo(o2propValue) * direction.modifier
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
