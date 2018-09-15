package pl.helenium.mockingbird

open class Model(private val data: Map<String, Any?>) {

    @Suppress("UNCHECKED_CAST")
    fun embeddedModel(path: String) = Model(data[path] as Map<String, Any?>)

    @Suppress("UNCHECKED_CAST")
    fun <T> embeddedList(path: String) = data[path] as List<T>

    @Suppress("UNCHECKED_CAST")
    fun <K, V> embeddedMap(path: String) = data[path] as Map<K, V>

    @Suppress("UNCHECKED_CAST")
    fun <T> getProperty(property: String) = data[property] as T

    fun asMap(): Map<String, Any?> = data

}

class MutableModel(private val data: MutableMap<String, Any?>) : Model(data) {

    operator fun set(property: String, value: Any?) {
        data[property] = value
    }

}

fun Model.toMutable() = MutableModel(asMap().toMutableMap())
