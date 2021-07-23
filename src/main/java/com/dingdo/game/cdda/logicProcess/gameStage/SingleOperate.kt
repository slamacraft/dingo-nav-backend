package com.dingdo.game.cdda.logicProcess.gameStage

import com.dingdo.game.cdda.data.component.DataFileLoader
import com.dingdo.game.cdda.data.model.Armor
import com.dingdo.game.cdda.data.model.Materials
import com.dingdo.game.cdda.data.model.common.translation
import com.dingdo.game.cdda.user.CddaUserInfo
import com.dingdo.msgHandle.stage.UserStage
import com.dingdo.robot.mirai.MsgSender
import com.dingdo.user.UserContext
import net.mamoe.mirai.event.events.MessageEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class SearchEntity : UserStage {

    @Autowired
    lateinit var gameDefaultStage: GameDefaultStage

    override fun rootStage(): KClass<out UserStage> = GameDefaultStage::class

    override fun valid(msgEvent: MessageEvent): Boolean {
        return msgEvent.getMsgText().startsWith("搜索")
    }

    override fun transition(msgEvent: MessageEvent): UserStage {
        return this
    }

    override fun process(msgEvent: MessageEvent): UserStage {
        val entity = msgEvent.getMsgText().removePrefix("搜索")
        val searchByName = DataFileLoader.searchByName(entity)
        MsgSender.sendMsg(msgEvent.subject, searchByName.toString())
        return gameDefaultStage
    }
}

@Component
class ViewArmor: UserStage{

    @Autowired
    lateinit var gameDefaultStage: GameDefaultStage

    override fun rootStage(): KClass<out UserStage> = GameDefaultStage::class

    override fun valid(msgEvent: MessageEvent): Boolean = msgEvent.getMsgText().startsWith("查看装备")

    override fun transition(msgEvent: MessageEvent): UserStage {
        return this
    }

    override fun process(msgEvent: MessageEvent): UserStage {
        val armorList = msgEvent.getGameUserInfo().armor.joinToString("\n") {
            val armor = DataFileLoader.dataIdMap[it.itemId] as Armor
            val material = armor.material.first()
            val materialInfo = DataFileLoader.dataIdMap[material] as Materials

            val adjIndex = (100 - it.durable) / (100 / (materialInfo.dmgAdj.size + 1)) - 1

            "${if (adjIndex >= 0) materialInfo.dmgAdj[adjIndex].translation() else ""}${DataFileLoader.dataIdMap[it.itemId]!!.name}"
        }

        MsgSender.sendMsg(msgEvent.subject, armorList)
        return gameDefaultStage
    }

}
