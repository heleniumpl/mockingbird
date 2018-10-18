package pl.helenium.mockingbird.model

@Suppress("UNCHECKED_CAST")
open class Model(protected var data: Map<String, Any?> = mapOf()) {

    fun exists(property: String) = data.containsKey(property)

    fun isList(property: String) = data[property] is List<*>

    fun isMap(property: String) = data[property] is Map<*, *>

    fun <T> getProperty(property: String, default: T? = null) = (data[property] ?: default) as T

    open fun asMap() = data

    // effectively Map<String, Any?>
    open fun embeddedModel(path: String) = maybeEmbeddedModel(path) ?: throw IllegalArgumentException()

    open fun maybeEmbeddedModel(path: String) = data[path]?.let { Model(it as Map<String, Any?>) }

    // effectively List<Map<String, Any?>>
    open fun embeddedModelList(path: String) = maybeEmbeddedModelList(path) ?: throw IllegalArgumentException()

    open fun maybeEmbeddedModelList(path: String) = data[path]?.let { it as List<Map<String, Any?>> }?.map(::Model)

    // effectively List<T>
    open fun <T> embeddedList(path: String) = maybeEmbeddedList<T>(path) ?: throw IllegalArgumentException()

    open fun <T> maybeEmbeddedList(path: String) = data[path] as? List<T>

    override fun toString() = "Model(data=$data)"

}

@Suppress("UNCHECKED_CAST")
class MutableModel(data: MutableMap<String, Any?>) : Model(data) {

    fun replace(model: Model) {
        data = model
            .toNewMutable()
            .asMap()
    }

    fun setProperty(property: String, value: Any?) {
        asMap()[property] = value
    }

    override fun asMap() = super.asMap() as MutableMap<String, Any?>

    override fun embeddedModel(path: String) = maybeEmbeddedModel(path) ?: throw IllegalArgumentException()

    override fun maybeEmbeddedModel(path: String) = asMap()[path]?.let { MutableModel(it as MutableMap<String, Any?>) }

    override fun embeddedModelList(path: String) = maybeEmbeddedModelList(path) ?: throw IllegalArgumentException()

    override fun maybeEmbeddedModelList(path: String) =
        asMap()[path]?.let { it as List<MutableMap<String, Any?>> }?.map(::MutableModel)

    override fun <T> embeddedList(path: String) = maybeEmbeddedList<T>(path) ?: throw IllegalArgumentException()

    override fun <T> maybeEmbeddedList(path: String) = asMap()[path] as? MutableList<T>

}

@Suppress("UNCHECKED_CAST")
fun Model.toNewMutable() = MutableModel(asMap().deepCopy() as MutableMap<String, Any?>)

private fun Any?.deepCopy(): Any? = when (this) {
    is List<*> -> this
        .map(Any?::deepCopy)
        .toMutableList()
    is Map<*, *> -> this
        .mapValues { (_, v) -> v.deepCopy() }
        .toMutableMap()
    else -> this
}

