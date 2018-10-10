package pl.helenium.mockingbird.model

interface LifecycleHandler {

    fun preCreate(
        context: Context,
        metaModel: MetaModel,
        actor: Actor?,
        model: MutableModel
    ) = Unit

    fun postCreate(
        context: Context,
        metaModel: MetaModel,
        actor: Actor?,
        model: MutableModel
    ) = Unit

    fun preUpdate(
        context: Context,
        metaModel: MetaModel,
        actor: Actor?,
        model: MutableModel
    ) = Unit

    fun postUpdate(
        context: Context,
        metaModel: MetaModel,
        actor: Actor?,
        model: MutableModel
    ) = Unit

    fun preDelete(
        context: Context,
        metaModel: MetaModel,
        actor: Actor?,
        model: Model
    ) = Unit

    fun postDelete(
        context: Context,
        metaModel: MetaModel,
        actor: Actor?,
        model: Model
    ) = Unit

}

fun Iterable<LifecycleHandler>.preCreate(
    context: Context,
    metaModel: MetaModel,
    actor: Actor?,
    model: MutableModel
) = forEach { it.preCreate(context, metaModel, actor, model) }

fun Iterable<LifecycleHandler>.postCreate(
    context: Context,
    metaModel: MetaModel,
    actor: Actor?,
    model: MutableModel
) = forEach { it.postCreate(context, metaModel, actor, model) }

fun Iterable<LifecycleHandler>.preUpdate(
    context: Context,
    metaModel: MetaModel,
    actor: Actor?,
    model: MutableModel
) = forEach { it.preUpdate(context, metaModel, actor, model) }

fun Iterable<LifecycleHandler>.postUpdate(
    context: Context,
    metaModel: MetaModel,
    actor: Actor?,
    model: MutableModel
) = forEach { it.postUpdate(context, metaModel, actor, model) }

fun Iterable<LifecycleHandler>.preDelete(
    context: Context,
    metaModel: MetaModel,
    actor: Actor?,
    model: Model
) = forEach { it.preDelete(context, metaModel, actor, model) }

fun Iterable<LifecycleHandler>.postDelete(
    context: Context,
    metaModel: MetaModel,
    actor: Actor?,
    model: Model
) = forEach { it.postDelete(context, metaModel, actor, model) }
