package pl.helenium.mockingbird.model

import kotlin.math.min

class Page<out T>(val items: List<T>) {

    fun <S> transformItems(elementTransformer: (T) -> S) = Page(items.map(elementTransformer))

}

data class PageRequest(val page: Int, val itemsPerPage: Int) {

    val firstItem
        get() = page * itemsPerPage

    val lastItemExclusive
        get() = firstItem + itemsPerPage

}

fun <T> List<T>.page(request: PageRequest) = Page(
    subList(request.firstItem, min(request.lastItemExclusive, size))
)

val noPaging = PageRequest(0, Int.MAX_VALUE)
