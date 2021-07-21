package com.dingdo.game.cdda.logicProcess.gameStage

import com.dingdo.msgHandle.stage.DefaultRootStage
import com.dingdo.msgHandle.stage.RootStage
import com.dingdo.msgHandle.stage.UserStage
import com.dingdo.robot.mirai.MsgSender
import net.mamoe.mirai.event.events.MessageEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class GameDefaultStage : RootStage {

    override fun order(): Int {
        return 2
    }

    override fun valid(msgEvent: MessageEvent): Boolean {
        return msgEvent.getMsgText().startsWith("开始游戏")
    }

    override fun process(msgEvent: MessageEvent): UserStage {
        MsgSender.sendMsg(msgEvent.subject, "欢迎来到《大灾变，黑耀之日》的世界")
        return super.process(msgEvent)
    }
}

@Component
class CloseGameStage : UserStage {
    @Autowired
    lateinit var gameDefaultStage: GameDefaultStage

    override fun rootStage(): UserStage = gameDefaultStage

    override fun valid(msgEvent: MessageEvent): Boolean = msgEvent.getMsgText().startsWith("退出游戏")

    override fun transition(msgEvent: MessageEvent): UserStage = this

    override fun process(msgEvent: MessageEvent): UserStage {
        MsgSender.sendMsg(msgEvent.subject, "保存并退出")
        return DefaultRootStage.defaultStage
    }

}
