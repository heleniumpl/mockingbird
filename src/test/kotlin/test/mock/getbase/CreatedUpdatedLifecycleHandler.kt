package pl.helenium.mockingbird.test.mock.getbase

import pl.helenium.mockingbird.model.Actor
import pl.helenium.mockingbird.model.Context
import pl.helenium.mockingbird.model.LifecycleHandler
import pl.helenium.mockingbird.model.MetaModel
import pl.helenium.mockingbird.model.MutableModel
import java.time.Instant
import java.time.temporal.ChronoUnit.SECONDS

object CreatedUpdatedLifecycleHandler : LifecycleHandler {

    private const val CREATED_AT = "created_at"

    private const val UPDATED_AT = "updated_at"

    override fun preCreate(context: Context, metaModel: MetaModel, actor: Actor?, model: MutableModel) =
        with(nowAsIso8601()) {
            listOf(CREATED_AT, UPDATED_AT).forEach { property ->
                model.setProperty(property, this)
            }
        }

    override fun preUpdate(context: Context, metaModel: MetaModel, actor: Actor?, model: MutableModel) {
        model.setProperty(UPDATED_AT, nowAsIso8601())
    }

    private fun nowAsIso8601() = Instant
        .now()
        .truncatedTo(SECONDS)
        .toString()

}
