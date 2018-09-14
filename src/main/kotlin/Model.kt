package pl.helenium.mockingbird

class Model(private val data: Map<String, Any?>) {

    @Suppress("UNCHECKED_CAST")
    fun embeddedModel(path: String) = Model(data[path] as Map<String, Any?>)

    @Suppress("UNCHECKED_CAST")
    fun <T> embeddedList(path: String) = data[path] as List<T>

    @Suppress("UNCHECKED_CAST")
    fun <K, V> embeddedMap(path: String) = data[path] as Map<K, V>

    operator fun get(property: String) = data[property]

    fun asMap() : Map<String, Any?> = data

}
