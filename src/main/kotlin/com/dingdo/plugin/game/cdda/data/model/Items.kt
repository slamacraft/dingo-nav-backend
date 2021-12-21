package com.dingdo.plugin.game.cdda.data.model

import com.dingdo.plugin.game.cdda.data.component.DataFileLoader
import com.dingdo.plugin.game.cdda.data.model.common.BaseData
import com.dingdo.plugin.game.cdda.data.model.common.translation
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.beans.BeanUtils

open class Generic : BaseData() {

    lateinit var description: String
    var price = 0
    lateinit var material: List<String>
    var cutting = 0 // 切割伤害
    var bashing = 0 // 重击伤害
    var toHit = 0 // 命中奖励

    override fun extends(): BaseData {
        val subData = Monster()
        BeanUtils.copyProperties(this, subData)
        return subData
    }

    override fun parse(rootNode: JsonNode): BaseData {
        super.parse(rootNode)

        description = rootNode.path("description").asText().translation()
        price = rootNode.path("price").asInt()
        material = rootNode.path("material").map { it.asText() }
        cutting = rootNode.path("cutting").asInt()
        bashing = rootNode.path("bashing").asInt()
        toHit = rootNode.path("to_hit").asInt()

        return this
    }

    override fun toString(): String {
        val materialList = material.map { DataFileLoader.dataIdMap[it]!!.name }

        return """
名称：$name
描述：$description
材质：$materialList${if (cutting > 0) "\n用它能造成 $cutting 点切割伤害" else ""}${if (bashing > 0) "\n用它能造成 $bashing 点重击伤害" else ""}
        """.trimIndent()
    }
}


class Armor : Generic() {

    lateinit var covers:List<String> // 覆盖部位
    var coverage = 80 // 覆盖百分比
    var warmth = 0 // 保暖度
    var encumbrance = 0 // 累赘度
    var environmentalProtection = 0 // 环境保护
    var materialThickness = 0 // 材料厚度

    override fun extends(): BaseData {
        val subData = Monster()
        BeanUtils.copyProperties(this, subData)
        return subData
    }

    override fun parse(rootNode: JsonNode): BaseData {
        super.parse(rootNode)

        covers = rootNode.path("covers").map { it.asText() }
        coverage = rootNode.path("coverage").asInt()
        warmth = rootNode.path("warmth").asInt()
        encumbrance = rootNode.path("encumbrance").asInt()
        environmentalProtection = rootNode.path("environmental_protection").asInt()
        materialThickness = rootNode.path("material_thickness").asInt()

        return this
    }

    override fun toString(): String {
        val coversList = covers.map { DataFileLoader.dataIdMap[it]!!.name }

        return super.toString() + "\n" + """
覆盖部位：$coversList
覆盖程度：$coverage %
保暖度：$warmth 
累赘度：$encumbrance 
材料厚度： $materialThickness
        """.trimIndent()
    }
}
