package pl.helenium.mockingbird

import java.util.concurrent.ConcurrentHashMap

class ModelCollection(private val metaModel: MetaModel) {

    private val models = ConcurrentHashMap<String, Model>()

    fun create(model: Model): Model {
        val mutableModel = model.toMutable()
        val idProperty = metaModel.id()
        val generatedId = idProperty.generate() ?: throw IllegalStateException("ID property has no generator specified!")
        mutableModel.setProperty(idProperty.name, generatedId)
        models[generatedId.toString()] = mutableModel
        return mutableModel
    }

    fun list(): Collection<Model> = models.values

    fun get(id: Any): Model? = models[id.toString()]

    fun update(id: Any, model: Model, updater: Updater = RestUpdater): Model {
        return models[id.toString()]
            ?.toMutable()
            ?.also { updater.update(it, model) }
            ?.also { models[id.toString()] = it }
            ?: throw NotFoundException()
    }

    fun delete(id: Any) = models.remove(id.toString())

}
