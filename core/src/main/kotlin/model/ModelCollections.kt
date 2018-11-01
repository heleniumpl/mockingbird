package pl.helenium.mockingbird.model

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

class ModelCollections(private val context: Context) {

    private val modelCollections = mutableMapOf<MetaModel, ModelCollection>()

    fun byMetaModel(metaModel: MetaModel) =
        modelCollections.computeIfAbsent(metaModel) { ModelCollection(context, it) }

}

class ModelCollection(private val context: Context, private val metaModel: MetaModel) {

    // TODO allow other backend than memory
    private val models: ConcurrentMap<String, MutableModel> = ConcurrentHashMap()

    fun create(actor: Actor?, model: Model): Model {
        val mutableModel = model.toNewMutable()
        preCreate(actor, mutableModel)
        val idProperty = metaModel.id()
        val generatedId = idProperty
            .type
            .generate() ?: throw IllegalStateException("ID property has no generator specified!")
        mutableModel.setProperty(idProperty.name, generatedId)
        metaModel.validate(context, actor, mutableModel)
        models[generatedId.toString()] = mutableModel
        postCreate(actor, mutableModel)
        return mutableModel
    }

    fun list(request: PageRequest? = null, orderBy: Comparator<Model>? = null): Page<Model> = models
        .values
        .toList()
        .sortedWith(orderBy ?: Order(metaModel.id()).toOrderBy())
        .page(request)

    fun get(id: Any): Model? = models[id.toString()]

    // FIXME how to get rid of this synchronized?
    fun update(actor: Actor?, id: Any, update: Model, updater: Updater = NaiveUpdater): MutableModel? =
        synchronized(id) {
            val existingModel = models[id.toString()]?.toNewMutable() ?: return null
            preUpdate(actor, existingModel, update)
            metaModel.validate(context, actor, existingModel)
            updater.update(existingModel, update)
            postUpdate(actor, existingModel)
            metaModel.validate(context, actor, existingModel)
            models[id.toString()] = existingModel
            existingModel
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

    private fun preUpdate(actor: Actor?, model: MutableModel, update: Model) = lifecycleHandlers()
        .preUpdate(context, metaModel, actor, model, update)

    private fun postUpdate(actor: Actor?, model: MutableModel) = lifecycleHandlers()
        .postUpdate(context, metaModel, actor, model)

    private fun preDelete(actor: Actor?, model: Model) = lifecycleHandlers()
        .preDelete(context, metaModel, actor, model)

    private fun postDelete(actor: Actor?, model: Model) = lifecycleHandlers()
        .postDelete(context, metaModel, actor, model)

    private fun lifecycleHandlers() = metaModel.lifecycleHandlers()

}

interface Updater {

    fun update(target: MutableModel, source: Model)

}

object NaiveUpdater : Updater {

    override fun update(target: MutableModel, source: Model) = target.replace(source)

}
