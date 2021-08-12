package com.dingdo.module.game.cdda.user

import com.dingdo.module.game.cdda.data.component.DataFileLoader
import com.dingdo.module.game.cdda.data.emuns.Type
import com.dingdo.module.game.cdda.data.model.Armor
import com.dingdo.module.game.cdda.data.model.Materials
import com.dingdo.module.game.cdda.data.model.Professions

class CddaUserInfo(val id: Long, val name: String) {

    var per = 8 // 感知
    var str = 8 // 力量
    var dex = 8 // 敏捷
    var int = 8 // 智力

    var hp = 100
    lateinit var description: String

    val items = ArrayList<Item>()
    val weapon = ArrayList<Item>()
    val armor = ArrayList<Item>()

    val traits = ArrayList<String>()

    lateinit var profession: String
    lateinit var professionName:String
    var sex = false // 性别 false = 男 ， true = 女

    fun registerProfession(professionId: String) {
        val professionInfo = DataFileLoader.dataIdMap[professionId] as Professions
        profession = professionId
        professionName = if (sex) professionInfo.femaleName else professionInfo.maleName

        description = if (sex) professionInfo.femaleDescription else professionInfo.maleDescription

        val itemList = ArrayList<String>(professionInfo.bothItems)
        itemList.addAll(
            if (sex)
                professionInfo.femaleItems
            else
                professionInfo.maleItems
        )

        itemList.forEach {
            val item = DataFileLoader.dataIdMap[it]
            if (item != null && item.type == Type.ARMOR) {
                armor.add(Item(it))
            } else {
                items.add(Item(it))
            }
        }

        professionInfo.traits.also { traits.addAll(it) }
    }

    override fun toString(): String {
        return """
名称：$name    性别：${if (sex) "女" else "男"}
职业：$professionName
描述：$description
        """.trimIndent()
    }
}

class BodyPart(val partId: String) {
    var hp = 0
    var encumbrance = 0 // 累赘度
}

class Item(val itemId: String) {
    var durable = 100   // 耐久值, 部分可使用的物品才有，比如护甲

    override fun toString(): String {
        return when (val item = DataFileLoader.dataIdMap[this.itemId]){
            is Armor -> {
                val material = item.material.first()
                val materialInfo = DataFileLoader.dataIdMap[material] as Materials
                "${materialInfo.getDurability(this.durable)}${DataFileLoader.dataIdMap[this.itemId]!!.name}"
            }
            else -> {
                DataFileLoader.dataIdMap[this.itemId]!!.name
            }
        }
    }
}
