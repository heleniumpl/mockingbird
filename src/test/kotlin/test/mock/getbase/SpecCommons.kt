package pl.helenium.mockingbird.test.mock.getbase

import pl.helenium.mockingbird.json.defaultObjectMapper
import pl.helenium.mockingbird.json.readStringKeyMap
import pl.helenium.mockingbird.model.Model
import pl.helenium.mockingbird.test.util.StatusAndBody
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.absoluteValue

fun randomLong() = ThreadLocalRandom
    .current()
    .nextLong()
    .absoluteValue

fun Any.toJson(): String = defaultObjectMapper.writeValueAsString(this)

fun StatusAndBody.model() = Model(defaultObjectMapper.readStringKeyMap(body))

fun Model.data() = embeddedModel("data")

fun Model.items() = embeddedModelList("items")

fun Model.meta() = embeddedModel("meta")

fun Model.id() = getProperty<Long>("id")
