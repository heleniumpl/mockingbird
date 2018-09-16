package pl.helenium.mockingbird

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper

internal val objectMapper = ObjectMapper()

fun ObjectMapper.readMap(payload: String) =
    readValue<Map<String, Any?>>(payload, object : TypeReference<Map<String, Any?>>() {})!!
