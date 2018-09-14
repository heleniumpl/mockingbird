package pl.helenium.mockingbird

class Model(private val data: Map<String, Any?>) {

    @Suppress("UNCHECKED_CAST")
    fun embeddedModel(path: String) = Model(data[path] as Map<String, Any?>)

    operator fun get(property: String) = data[property]

    fun asMap() : Map<String, Any?> = data

}
