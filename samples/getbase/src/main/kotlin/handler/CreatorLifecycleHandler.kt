package pl.helenium.mockingbird.test.mock.getbase.handler

import pl.helenium.mockingbird.model.Actor
import pl.helenium.mockingbird.model.Context
import pl.helenium.mockingbird.model.LifecycleHandler
import pl.helenium.mockingbird.model.MetaModel
import pl.helenium.mockingbird.model.MutableModel

object CreatorLifecycleHandler : LifecycleHandler {

    override fun preCreate(
        context: Context,
        metaModel: MetaModel,
        actor: Actor?,
        model: MutableModel
    ) {
        if (actor == null) throw IllegalStateException("Cannot set creator_id due to actor being null!")

        model.setProperty("creator_id", actor.id)
    }

}
