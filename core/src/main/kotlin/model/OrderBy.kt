package pl.helenium.mockingbird.model

import pl.helenium.mockingbird.model.Direction.ASC

class OrderBy(vararg orders: Comparator<Model>) : Comparator<Model> {

    private val comparator = orders
        .reduce { acc, v-> acc.thenComparing(v) }

    override fun compare(o1: Model, o2: Model) = comparator.compare(o1, o2)

}

class Order(val property: String, val direction: Direction = ASC) : Comparator<Model> {

    override fun compare(o1: Model, o2: Model): Int {
        val o1propValue = o1.getProperty<Comparable<Any>>(property)
        val o2propValue = o2.getProperty<Comparable<Any>>(property)

        return o1propValue.compareTo(o2propValue) * direction.modifier
    }

    fun toOrderBy() = OrderBy(this)

}

enum class Direction(val modifier: Int) {
    ASC(1),
    DESC(-1),
    ;
}
