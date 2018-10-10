package pl.helenium.mockingbird.model

import java.util.concurrent.ConcurrentHashMap

class ModelCollections(private val context: Context) {

    private val modelCollections = mutableMapOf<MetaModel, ModelCollection>()

    fun byMetaModel(metaModel: MetaModel) =
        modelCollections.computeIfAbsent(metaModel) { ModelCollection(context, it) }

}

class ModelCollection(private val context: Context, private val metaModel: MetaModel) {

    private val models = ConcurrentHashMap<String, MutableModel>()

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

    fun update(actor: Actor?, id: Any, update: Model, updater: Updater = NaiveUpdater) =
        models.computeIfPresent(id.toString()) { _, existingModel ->
            preUpdate(actor, existingModel)
            updater
                .update(existingModel, update)
                .also { postUpdate(actor, it) }
        }

    fun delete(actor: Actor?, id: Any): Model? {
        preDelete(actor, models[id.toString()] ?: return null)
        return models
            .remove(id.toString())
            ?.also { postDelete(actor, it) }
    }

    private fun preCreate(actor: Actor?, model: MutableModel) = lifecycleHandlers()
        .preCreate(context, metaModel, actor, model)

    private fun postCreate(actor: Actor?, model: MutableModel) = lifecycleHandlers()
        .postCreate(context, metaModel, actor, model)

    private fun preUpdate(actor: Actor?, model: MutableModel) = lifecycleHandlers()
        .preUpdate(context, metaModel, actor, model)

    private fun postUpdate(actor: Actor?, model: MutableModel) = lifecycleHandlers()
        .postUpdate(context, metaModel, actor, model)

    private fun preDelete(actor: Actor?, model: Model) = lifecycleHandlers()
        .preDelete(context, metaModel, actor, model)

    private fun postDelete(actor: Actor?, model: Model) = lifecycleHandlers()
        .postDelete(context, metaModel, actor, model)

    private fun lifecycleHandlers() = metaModel.lifecycleHandlers()

}

interface Updater {

    fun update(target: Model, source: Model): MutableModel

}

object NaiveUpdater : Updater {

    override fun update(target: Model, source: Model): MutableModel = source.toMutable()

}
