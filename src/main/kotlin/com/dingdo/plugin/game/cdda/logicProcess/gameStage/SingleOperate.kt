package com.dingdo.plugin.game.cdda.logicProcess.gameStage

import com.dingdo.plugin.game.cdda.data.component.DataFileLoader
import com.dingdo.core.msgHandle.UserStage
import com.dingdo.core.robot.mirai.MsgSender
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
        return msgEvent.getText().startsWith("搜索")
    }

    override fun transition(msgEvent: MessageEvent): UserStage {
        return this
    }

    override fun process(msgEvent: MessageEvent): UserStage {
        val entity = msgEvent.getText().removePrefix("搜索")
        MsgSender.sendMsg(msgEvent, DataFileLoader.searchByName(entity).toString())
        return gameDefaultStage
    }
}

@Component
class ViewArmor: UserStage {

    @Autowired
    lateinit var gameDefaultStage: GameDefaultStage

    override fun rootStage(): KClass<out UserStage> = GameDefaultStage::class

    override fun valid(msgEvent: MessageEvent): Boolean = msgEvent.getText().startsWith("装备栏")

    override fun transition(msgEvent: MessageEvent): UserStage {
        return this
    }

    override fun process(msgEvent: MessageEvent): UserStage {
        val armorList = msgEvent.getGameUserInfo().armor
            .joinToString("\n") { it.toString() }

        MsgSender.sendMsg(msgEvent, armorList)
        return gameDefaultStage
    }

}
