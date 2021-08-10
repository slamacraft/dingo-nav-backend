package com.dingdo.game.cdda.logicProcess.gameStage

import com.dingdo.msgHandle.stage.DefaultRootStage
import com.dingdo.msgHandle.stage.RootStage
import com.dingdo.msgHandle.stage.UserStage
import com.dingdo.robot.mirai.MsgSender
import net.mamoe.mirai.event.events.MessageEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class GameDefaultStage : RootStage {

    override fun rootStage(): KClass<out UserStage> {
        // todo 通过将上级节点指定为本身，从而屏蔽该功能节点
        return GameDefaultStage::class
    }

    override fun order(): Int {
        return 2
    }

    override fun valid(msgEvent: MessageEvent): Boolean {
        return msgEvent.getText().startsWith("开始游戏")
    }

    override fun process(msgEvent: MessageEvent): UserStage {
        MsgSender.sendMsg(msgEvent.subject, "欢迎来到《大灾变，黑耀之日》的世界")
        return super.process(msgEvent)
    }
}

@Component
class CloseGame : UserStage {
    @Autowired
    lateinit var gameDefaultStage: GameDefaultStage

    override fun rootStage(): KClass<out UserStage> = GameDefaultStage::class

    override fun valid(msgEvent: MessageEvent): Boolean = msgEvent.getText().startsWith("退出游戏")

    override fun transition(msgEvent: MessageEvent): UserStage = this

    override fun process(msgEvent: MessageEvent): UserStage {
        MsgSender.sendMsg(msgEvent.subject, "保存并退出")
        return DefaultRootStage.defaultStage
    }

}
