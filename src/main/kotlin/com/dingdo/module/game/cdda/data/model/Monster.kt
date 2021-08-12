package com.dingdo.module.game.cdda.data.model

import com.dingdo.module.game.cdda.data.model.common.BaseData
import com.dingdo.module.game.cdda.data.model.common.translation
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.beans.BeanUtils

class Monster : BaseData() {

    lateinit var description: String
    var hp: Int = 0
    lateinit var volume: String
    lateinit var weight: String
    lateinit var color: String
    lateinit var defaultFaction: List<String>
    lateinit var bodyType: String
    var speed: Int = 0
    lateinit var material: List<String>

    lateinit var species: List<String>  // 物种
    var meleeSkill: Int = 0 // 近战中的怪物技能，从“0-10”，“4”是普通暴民
    var meleeDice: Int = 0  // 近战骰子个数
    var meleeDiceSides: Int = 0 // 近战骰子面数
    var meleeCut: Int = 0   // 近战切割，对衣物的损伤
    var armorBash: Int = 0  // 冲击伤害保护
    var armorCut: Int = 0   // 切割伤害保护
    var armorBullet: Int = 0    // 弹药伤害保护


    override fun parse(rootNode: JsonNode): BaseData {
        super.parse(rootNode)

        description = rootNode.path("description").asText().translation()
        bodyType = rootNode.path("bodytype").asText()
        species = rootNode.path("species").map { it.asText() }
        volume = rootNode.path("volume").asText()
        weight = rootNode.path("weight").asText()
        hp = rootNode.path("hp").asInt()
        defaultFaction = rootNode.path("default_faction").map { it.asText() }
        speed = rootNode.path("speed").asInt()
        material = rootNode.path("material").map { it.asText().translation() }
        color = rootNode.path("color").asText().translation()
        meleeSkill = rootNode.path("meleeSkill").asInt()
        meleeDice = rootNode.path("melee_dice").asInt()
        meleeDiceSides = rootNode.path("melee_dice_sides").asInt()
        meleeCut = rootNode.path("melee_cut").asInt()
        armorBash = rootNode.path("armor_bash").asInt()
        armorCut = rootNode.path("armor_cut").asInt()
        armorBullet = rootNode.path("armor_bullet").asInt()

        return this
    }

    override fun extends(): BaseData {
        val subMonster =  Monster()
        BeanUtils.copyProperties(this, subMonster)
        return subMonster
    }

    override fun toString(): String {
        return """
            名称：$name
            描述：$description
        """.trimIndent()
    }

}

