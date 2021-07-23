package com.dingdo.game.cdda.data.model

import com.dingdo.game.cdda.data.model.common.BaseData
import com.dingdo.game.cdda.data.model.common.translation
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.beans.BeanUtils

class Professions : BaseData() {

    lateinit var maleName: String
    lateinit var femaleName: String
    lateinit var description: String
    lateinit var maleDescription: String
    lateinit var femaleDescription: String
    var points = 0
    lateinit var bothItems: List<String>
    lateinit var maleItems: List<String>
    lateinit var femaleItems: List<String>
    lateinit var traits:List<String>

    override fun parse(rootNode: JsonNode): BaseData {
        super.parse(rootNode)

        val nameJsonNode = rootNode.path("name")
        if (nameJsonNode.path("male").asText().isNotBlank()) {
            maleName = ("profession_male\u0004" + nameJsonNode.path("male").asText()).translation()
            femaleName = ("profession_female\u0004" + nameJsonNode.path("female").asText()).translation()
            name = maleName
        } else {
            maleName = name
            femaleName = name
        }
        description = rootNode.path("description").asText()
        maleDescription = ("prof_desc_male\u0004$description").translation()
        femaleDescription = ("prof_desc_female\u0004$description").translation()
        val itemJsonNode = rootNode.path("items")
        bothItems = itemJsonNode.path("both").path("items").map { it.asText() }
        maleItems = itemJsonNode.path("male").map { it.asText() }
        femaleItems = itemJsonNode.path("female").map { it.asText() }
        traits = rootNode.path("traits").map { it.asText() }

        return this
    }

    override fun alias(): String {
        return femaleName
    }

    override fun toString(): String {
        return """
职业：$maleName($femaleName)
描述：$maleDescription
        """.trimIndent()
    }

    override fun extends(): BaseData {
        val subData = Monster()
        BeanUtils.copyProperties(this, subData)
        return subData
    }
}
