package com.dingdo.game.cdda.data.model

import com.dingdo.game.cdda.data.model.common.BaseData
import com.dingdo.game.cdda.data.model.common.translation
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.beans.BeanUtils

class Materials : BaseData() {

    var bashResist = 0 // 冲击抗性
    var cutResist = 0  // 切割抗性
    var bulletResist = 0   // 弹药抗性
    var acidResist = 0 // 耐酸
    var elecResist = 0 // 绝缘
    var fireResist = 0 // 耐火
    lateinit var dmgAdj: List<String>  // 损伤描述
    var edible = false // 可食用
    var rotting = false // 会腐烂
    var soft = false // 柔软的

    override fun extends(): BaseData {
        val subData = Monster()
        BeanUtils.copyProperties(this, subData)
        return subData
    }

    override fun parse(rootNode: JsonNode): BaseData {
        super.parse(rootNode)

        bashResist = rootNode.path("bash_resist").asInt()
        cutResist = rootNode.path("cut_resist").asInt()
        bulletResist = rootNode.path("bullet_resist").asInt()
        acidResist =  rootNode.path("acid_resist").asInt()
        elecResist = rootNode.path("elec_resist").asInt()
        fireResist = rootNode.path("fire_resist").asInt()
        dmgAdj = rootNode.path("dmg_adj").map { it.asText().translation() }
        edible =  rootNode.path("edible").asBoolean()
        rotting =  rootNode.path("edible").asBoolean()
        soft =  rootNode.path("soft").asBoolean()

        return this
    }

    override fun toString(): String {
        return """
名称：$name
冲击耐性：$bashResist
切割耐性：$cutResist
弹药耐性：$bulletResist
酸耐性：$acidResist
绝缘性：$elecResist
阻燃性：$fireResist
        """.trimIndent()
    }


    fun getDurability(durablePer: Int): String {
        val adjIndex = (100 - durablePer) / (100 / (this.dmgAdj.size + 1)) - 1
        return if (adjIndex >= 0) this.dmgAdj[adjIndex] else ""
    }

}
