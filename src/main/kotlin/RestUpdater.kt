package pl.helenium.mockingbird

interface Updater {

    fun update(target: MutableModel, source: Model)

}

object RestUpdater : Updater {

    override fun update(target: MutableModel, source: Model) {
        source
            .asMap()
            .forEach { (prop, newValue) ->
                handleProperty(target, prop, newValue)
            }
    }

    @Suppress("UNCHECKED_CAST")
    private fun handleProperty(target: MutableModel, property: String, newValue: Any?) {
        if (newValue == null || !target.exists(property)) {
            target.setProperty(property, newValue)
            return
        }

        if (target.isMap(property) && newValue !is Map<*, *>) throw IllegalArgumentException()
        if (target.isList(property) && newValue !is List<*>) throw IllegalArgumentException()
        if (newValue is Map<*, *> && !target.isMap(property)) throw IllegalArgumentException()
        if (newValue is List<*> && !target.isList(property)) throw IllegalArgumentException()

        if (target.isMap(property)) {
            update(target.embeddedModel(property), Model(newValue as Map<String, Any?>))
            return
        }

        target.setProperty(property, newValue)
    }

}
