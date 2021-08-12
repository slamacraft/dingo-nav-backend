package com.dingdo.module.game.cdda.data.model

import com.dingdo.module.game.cdda.data.model.common.BaseData
import com.dingdo.module.game.cdda.data.model.common.translation
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.beans.BeanUtils

class Mutations : BaseData() {

    var points = 0
    var ugliness = 0 // 丑陋度，负数加美观度
    var cutDmgBonus = 0 // 徒手切割伤害加成
    var pierceDmgBonus = 0 // 徒手穿刺伤害加成
    var bashDmgBonus = 0 // 徒手打击伤害加成
    lateinit var randCutBonus: List<WeaponDamageBonus>    // 武器切割伤害加成
    lateinit var randBashBonus: List<WeaponDamageBonus>  // 武器打击伤害加成
    lateinit var description: String
    var startingTrait = false  // 初始特质
    var valid = false // 可变异
    lateinit var prereqs: List<String>   // 变异先决条件1
    lateinit var prereqs2: List<String>  // 变异先决条件2
    lateinit var cancels: List<String>   // 变异成功后取消这些特质
    lateinit var changesTo: List<String>    // 可以进一步变异
    var passiveMods = ModChange()     // /使用列出的值增加属性。负数表示统计量减少
    lateinit var encumbranceAlways: Map<String, Int>
    var armor = HashMap<String, BodyArmor>()

    // 加法
    var healingAwake = 0 // 醒着时的自愈率变化 -0.002, 加
    var healingResting = 0 // 休息时的自愈率变化 -0.25 加
    var metabolismModifier = 0 // 代谢
    var hpModifier = 0 // 血量
    var fatigueModifier = 0 // 疲劳

    var movecostModifier = 1 // 移动花费变化率 1.25，这是乘以
    // movecost_flatground_modifier, movecost_obstacle_modifier * 0.75

    // 乘法
    var hearingModifier = 1 // 听力
    var noiseModifier = 1 // 噪声变化 乘以
    var dodgeModifier = 1 // 闪避变化 乘以
    var speedModifier = 1 // 速度
    var mendingModifier = 1 // 回血
    var weightCapacityModifier = 1 // 负重
    var consumeTimeModifier = 1 // 恰东西时间


    override fun parse(rootNode: JsonNode): BaseData {
        super.parse(rootNode)

        fun buildWeaponDamageBonus(jsonNode: JsonNode): WeaponDamageBonus {
            val min = jsonNode.path("min").asInt()
            val max = jsonNode.path("max").asInt()
            return WeaponDamageBonus(min, max)
        }

        points = rootNode.path("points").asInt()
        ugliness = rootNode.path("ugliness").asInt()
        cutDmgBonus = rootNode.path("cut_dmg_bonus").asInt()
        pierceDmgBonus = rootNode.path("pierce_dmg_bonus").asInt()
        bashDmgBonus = rootNode.path("bash_dmg_bonus").asInt()

        randCutBonus = rootNode.path("rand_cut_bonus").map { buildWeaponDamageBonus(it) }
        randBashBonus = rootNode.path("rand_bash_bonus").map { buildWeaponDamageBonus(it) }
        description = rootNode.path("description").asText().translation()
        startingTrait = rootNode.path("starting_trait").asBoolean()
        valid = rootNode.path("valid").asBoolean()
        prereqs = rootNode.path("prereqs").map { it.asText() }
        prereqs2 = rootNode.path("prereqs2").map { it.asText() }
        cancels = rootNode.path("cancels").map { it.asText() }
        changesTo = rootNode.path("changes_to").map { it.asText() }
        val modsJson = rootNode.path("passive_mods")
        passiveMods.perMod = modsJson.path("per_mod").asInt()
        passiveMods.strMod = modsJson.path("str_mod").asInt()
        passiveMods.dexMod = modsJson.path("dex_mod").asInt()
        passiveMods.intMod = modsJson.path("int_mod").asInt()
        encumbranceAlways =
            rootNode.path("encumbrance_always").associateBy({ it.first().asText() }, { it.last().asInt() })
        rootNode.path("armor").forEach {
            val bash = it.path("bash").asInt()
            val cut = it.path("cut").asInt()
            val bullet = it.path("bullet").asInt()
            it.path("parts").associateBy({ part -> part.asText() }, { part ->
                val bodyArmor = BodyArmor()
                bodyArmor.part = part.asText()
                bodyArmor.bash = bash
                bodyArmor.cut = cut
                bodyArmor.bullet = bullet
                bodyArmor
            }).also { partArmorMap -> armor.putAll(partArmorMap) }
        }

        healingAwake = rootNode.path("healing_awake").asInt()
        healingResting = rootNode.path("healing_resting").asInt()
        metabolismModifier = rootNode.path("metabolism_modifier").asInt()
        hpModifier = rootNode.path("hp_modifier").asInt()
        fatigueModifier = rootNode.path("fatigue_modifier").asInt()

        val moveCost = rootNode.path("movecost_modifier").asInt()
        val flatMoveCost = rootNode.path("movecost_flatground_modifier").asInt()
        val obstacleMoveCost = rootNode.path("movecost_obstacle_modifier").asInt()
        if (moveCost + flatMoveCost + obstacleMoveCost > 0)
            movecostModifier = minOf(moveCost, flatMoveCost, obstacleMoveCost)

        hearingModifier = rootNode.path("hearing_modifier").asInt()
        noiseModifier = rootNode.path("noise_modifier").asInt()
        dodgeModifier = rootNode.path("dodge_modifier").asInt()
        speedModifier = rootNode.path("speed_modifier").asInt()
        mendingModifier = rootNode.path("mending_modifier").asInt()
        weightCapacityModifier = rootNode.path("weight_capacity_modifier").asInt()
        consumeTimeModifier = rootNode.path("consume_time_modifier").asInt()

        return this
    }

    override fun toString(): String {
        return """
名称：$name
描述：$description
        """.trimIndent()
    }

    override fun extends(): BaseData {
        val subData = Monster()
        BeanUtils.copyProperties(this, subData)
        return subData
    }
}

class WeaponDamageBonus constructor(var min: Int, var max: Int) {
}

class ModChange {
    var perMod = 0 // 感知
    var strMod = 0 // 力量
    var dexMod = 0 // 敏捷
    var intMod = 0 // 智力
}

class BodyArmor {
    lateinit var part: String
    var bash = 0
    var cut = 0
    var bullet = 0
}
