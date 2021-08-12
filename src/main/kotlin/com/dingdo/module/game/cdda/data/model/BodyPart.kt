package com.dingdo.module.game.cdda.data.model

import com.dingdo.module.game.cdda.data.model.common.BaseData
import com.dingdo.module.game.cdda.data.model.common.translation
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.beans.BeanUtils

class BodyPart : BaseData() {

    lateinit var encumbranceText: String    // 累赘影响描述
    lateinit var mainPart: String   // 主体部分，主体没了这个部位也没了
    lateinit var connectedTo: String    // 连接处，连接处没了这个也没了
    var hitSize = 0 // 受击大小
    var baseHp = 0
    lateinit var smashMessage: String // 粉碎物体时的描述
    var isLimb = false  // 是否是四肢

    override fun extends(): BaseData {
        val subData = Monster()
        BeanUtils.copyProperties(this, subData)
        return subData
    }

    override fun parse(rootNode: JsonNode): BaseData {
        super.parse(rootNode)

        encumbranceText = rootNode.path("encumbrance_text").asText().translation()
        mainPart = rootNode.path("main_part").asText()
        connectedTo = rootNode.path("connected_to").asText()
        hitSize = rootNode.path("hit_size").asInt()
        baseHp = rootNode.path("base_hp").asInt()
        smashMessage = rootNode.path("smash_message").asText().translation()
        isLimb = rootNode.path("is_limb").asBoolean()

        return this
    }

    override fun toString(): String {
        return name
    }
}
