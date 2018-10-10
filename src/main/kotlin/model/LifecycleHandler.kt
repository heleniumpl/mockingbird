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
