package pl.helenium.mockingbird.model

import pl.helenium.mockingbird.model.Direction.ASC

class OrderBy(val property: String, val direction: Direction = ASC)

enum class Direction(val modifier: Int) {
    ASC(1),
    DESC(1),
    ;
}

class OrderByComparator(private val orderBy: OrderBy) : Comparator<Model> {

    override fun compare(o1: Model, o2: Model): Int {
        val o1propValue = o1.getProperty<Comparable<Any>>(orderBy.property)
        val o2propValue = o2.getProperty<Comparable<Any>>(orderBy.property)

        return o1propValue.compareTo(o2propValue) * orderBy.direction.modifier
    }

}
