package pl.helenium.mockingbird.json

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import pl.helenium.mockingbird.model.Model

internal val defaultObjectMapper = ObjectMapper()

fun ObjectMapper.readStringKeyMap(payload: String): Map<String, Any?> =
    readValue<Map<String, Any?>>(payload, object : TypeReference<Map<String, Any?>>() {})

fun jsonRequestParser(body: String) = Model(defaultObjectMapper.readStringKeyMap(body))

fun jsonRequestWriter(model: Model): String = defaultObjectMapper.writeValueAsString(model.asMap())
