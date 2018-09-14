package pl.helenium.mockingbird

class ModelCollection {

    private val models = mutableMapOf<String, Model>()

    fun create(model: Model) = model.also {
        models["1"] = it
    }

}
