package pl.helenium.mockingbird

class ModelCollection(private val metaModel: MetaModel) {

    private val models = mutableMapOf<String, Model>()

    fun create(model: Model): Model {
        val mutableModel = model.toMutable()
        val idProperty = metaModel.id()
        val generatedId = idProperty.generate() ?: throw IllegalStateException("ID property has no generator specified!")
        mutableModel[idProperty.name] = generatedId
        models[generatedId.toString()] = mutableModel
        return mutableModel
    }

}
