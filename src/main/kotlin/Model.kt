package pl.helenium.mockingbird

@Suppress("UNCHECKED_CAST")
open class Model(private val data: Map<String, Any?> = mapOf()) {

    fun embeddedModel(path: String) = Model(data[path] as Map<String, Any?>)

    fun embeddedModelList(path: String) = (data[path] as List<Map<String, Any?>>).map(::Model)

    fun <T> embeddedList(path: String) = data[path] as List<T>

    fun <K, V> embeddedMap(path: String) = data[path] as Map<K, V>

    fun <T> getProperty(property: String) = data[property] as T

    fun asMap(): Map<String, Any?> = data

}

class MutableModel(private val data: MutableMap<String, Any?>) : Model(data) {

    fun setProperty(property: String, value: Any?) {
        data[property] = value
    }

}

fun Model.toMutable() = MutableModel(asMap().toMutableMap())
