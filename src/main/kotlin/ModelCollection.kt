package pl.helenium.mockingbird

class ModelCollection(private val metaModel: MetaModel) {

    private val models = mutableMapOf<String, Model>()

    fun create(model: Model): Model {
        val mutableModel = model.toMutable()
        val idProperty = metaModel.id()
        val generatedId = idProperty.generate() ?: throw IllegalStateException("ID property has no generator specified!")
        mutableModel.setProperty(idProperty.name, generatedId)
        models[generatedId.toString()] = mutableModel
        return mutableModel
    }

    fun get(id: Any): Model? = models[id.toString()]

    fun delete(id: Any) = models.remove(id.toString())

}
