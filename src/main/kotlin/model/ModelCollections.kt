package pl.helenium.mockingbird.model

import java.util.concurrent.ConcurrentHashMap

class ModelCollections(private val context: Context) {

    private val modelCollections = mutableMapOf<MetaModel, ModelCollection>()

    fun byMetaModel(metaModel: MetaModel) =
        modelCollections.computeIfAbsent(metaModel) { ModelCollection(context, it) }

}

class ModelCollection(private val context: Context, private val metaModel: MetaModel) {

    private val models = ConcurrentHashMap<String, Model>()

    fun create(actor: Actor?, model: Model): Model {
        val mutableModel = model.toMutable()
        preCreate(actor, mutableModel)
        val idProperty = metaModel.id()
        val generatedId = idProperty.generate()
            ?: throw IllegalStateException("ID property has no generator specified!")
        mutableModel.setProperty(idProperty.name, generatedId)
        models[generatedId.toString()] = mutableModel
        postCreate(actor, mutableModel)
        return mutableModel
    }

    fun list(): Collection<Model> = models.values

    fun get(id: Any): Model? = models[id.toString()]

    fun update(id: Any, update: Model, updater: Updater = NaiveUpdater) =
        models.computeIfPresent(id.toString()) { _, existingModel ->
            updater.update(existingModel, update)
        }

    fun delete(id: Any) = models.remove(id.toString())

    private fun preCreate(actor: Actor?, model: MutableModel) = metaModel
        .lifecycleHandlers()
        .preCreate(context, metaModel, actor, model)

    private fun postCreate(actor: Actor?, model: MutableModel) = metaModel
        .lifecycleHandlers()
        .postCreate(context, metaModel, actor, model)

}

interface Updater {

    fun update(target: Model, source: Model): MutableModel

}

object NaiveUpdater : Updater {

    override fun update(target: Model, source: Model): MutableModel = source.toMutable()

}
