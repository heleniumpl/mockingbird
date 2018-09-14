package pl.helenium.mockingbird

class ModelCollection(private val metaModel: MetaModel) {

    private val models = mutableMapOf<String, Model>()

    fun create(model: Model): Model {
        val id = 1
        val mutableModel = model.toMutable()
        mutableModel.setProperty(metaModel.id().name, id)
        models[id.toString()] = mutableModel
        return mutableModel
    }

}
