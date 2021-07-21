package com.dingdo.game.cdda.logicProcess.gameStage

import com.dingdo.game.cdda.data.component.DataFileLoader
import com.dingdo.msgHandle.stage.UserStage
import com.dingdo.robot.mirai.MsgSender
import net.mamoe.mirai.event.events.MessageEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SearchEntity : UserStage {

    @Autowired
    lateinit var gameDefaultStage: GameDefaultStage

    override fun rootStage(): UserStage {
        return gameDefaultStage
    }

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
