package pl.helenium.mockingbird.model

class Page<out T>(val items: Collection<T>) {

    fun <S> transformItems(elementTransformer: (T) -> S) = Page(items.map(elementTransformer))

}

class PageRequest(page: Int, itemsPerPage: Int) {

    val firstItem = page * itemsPerPage

    val lastItemExclusive = firstItem + itemsPerPage

}

fun <T> List<T>.page(request: PageRequest?) = Page(
    if (request == null)
        this
    else
        subList(request.firstItem, kotlin.math.min(request.lastItemExclusive, size))
)
