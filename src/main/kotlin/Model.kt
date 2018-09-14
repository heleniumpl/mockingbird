package pl.helenium.mockingbird

class Model(private val data: Map<String, Any?>) {

    @Suppress("UNCHECKED_CAST")
    fun embeddedModel(path: String) = Model(data[path] as Map<String, Any?>)

}
