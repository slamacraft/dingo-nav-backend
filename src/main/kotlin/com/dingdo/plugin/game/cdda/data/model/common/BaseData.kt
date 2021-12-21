package com.dingdo.plugin.game.cdda.data.model.common

import com.dingdo.plugin.game.cdda.data.component.GettextTranslator
import com.dingdo.plugin.game.cdda.data.emuns.Type
import com.fasterxml.jackson.databind.JsonNode


abstract class BaseData {
    lateinit var id: String
    lateinit var type: Type
    lateinit var name: String

    open fun parse(rootNode: JsonNode): BaseData {
        id = rootNode.path("id").asText()
        type = Type.getEnum(rootNode.path("type").asText()).get()
        name = rootNode.path("name").path("str").asText().translation()
            .ifBlank { rootNode.path("name").path("str_sp").asText().translation() }
            .ifBlank { rootNode.path("name").asText().translation() }
        return this
    }

    abstract fun extends(): BaseData

    open fun alias():String{
        return name
    }
}

fun String.translation(): String {
    return GettextTranslator.translation(this)
}
