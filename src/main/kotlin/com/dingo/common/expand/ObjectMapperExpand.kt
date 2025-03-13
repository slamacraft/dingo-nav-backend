package com.dingo.common.expand

import com.dingo.common.ObjectMappers
import com.fasterxml.jackson.databind.JsonNode
import kotlin.reflect.KClass

fun <T : Any> JsonNode.caseTo(clazz: KClass<T>): T {
    return ObjectMappers.jsonMapper.readValue(this.toString(), clazz.java)
}
