package com.dingdo.game.cdda.data.model.common

import com.dingdo.game.cdda.data.component.GettextTranslator
import com.dingdo.game.cdda.data.emuns.Type
import com.fasterxml.jackson.databind.JsonNode


abstract class BaseData {
    lateinit var id: String
    lateinit var type: Type
    lateinit var name: String

    open fun parse(rootNode: JsonNode): BaseData {
        id = rootNode.path("id").asText()
        type = Type.getEnum(rootNode.path("type").asText()).get()
        name = rootNode.path("name").path("str").asText().translation()
        return this
    }

    fun String.translation():String{
        return GettextTranslator.translation(this)
    }
}

