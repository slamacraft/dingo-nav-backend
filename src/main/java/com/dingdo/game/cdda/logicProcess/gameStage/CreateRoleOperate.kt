package com.dingdo.game.cdda.logicProcess.gameStage

import com.dingdo.game.cdda.data.component.DataFileLoader
import com.dingdo.game.cdda.data.emuns.Type
import com.dingdo.game.cdda.user.CddaUserInfo
import com.dingdo.msgHandle.stage.UserStage
import com.dingdo.robot.mirai.MsgSender
import com.dingdo.user.UserContext
import net.mamoe.mirai.event.events.MessageEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class CreateRoleAndSelectSex : UserStage {

    @Autowired
    lateinit var selectProfession: SelectProfession

    @Autowired
    lateinit var gameDefaultStage: GameDefaultStage

    override fun rootStage(): KClass<out UserStage> = GameDefaultStage::class

    override fun valid(msgEvent: MessageEvent): Boolean = msgEvent.getText().startsWith("创建角色")

    override fun transition(msgEvent: MessageEvent): UserStage {
        if (!selectProfession.valid(msgEvent)) {
            MsgSender.sendMsg(msgEvent.subject, "啊这")
            return this
        }
        msgEvent.getGameUserInfo().sex = msgEvent.getText() == "女"
        return selectProfession
    }

    override fun process(msgEvent: MessageEvent): UserStage {

        val info = UserContext.getUser(msgEvent.sender.id).getInfo(CddaUserInfo::class)
        if (info != null) {
            MsgSender.sendMsg(msgEvent, "您好像已经有角色了")
            return gameDefaultStage
        }

        MsgSender.sendMsg(msgEvent.subject, "您的性别是？")
        return this
    }
}

@Component
class SelectProfession : UserStage {
    @Autowired
    lateinit var gameDefaultStage: GameDefaultStage

    @Autowired
    lateinit var confirmRole: ConfirmRole

    override fun rootStage(): KClass<out UserStage> = CreateRoleAndSelectSex::class

    override fun valid(msgEvent: MessageEvent): Boolean =
        msgEvent.getText() == "男" || msgEvent.getText() == "女"

    override fun transition(msgEvent: MessageEvent): UserStage {
        val profession = msgEvent.getText()
        val professions = DataFileLoader.dataMap[Type.PROFESSION.typeName]
        val profInfo = professions!!.dataNameMap[profession]
        if (profInfo == null) {
            MsgSender.sendMsg(msgEvent, "这个职业的信息好像还没有录入，换一个吧")
            return this
        }
        msgEvent.getGameUserInfo().registerProfession(profInfo.id)
        return confirmRole
    }

    override fun process(msgEvent: MessageEvent): UserStage {
        MsgSender.sendMsg(msgEvent, "您的职业是？")
        return this
    }

}


@Component
class ConfirmRole : UserStage {

    @Autowired
    lateinit var gameDefaultStage: GameDefaultStage

    override fun rootStage(): KClass<out UserStage> = SelectProfession::class

    override fun valid(msgEvent: MessageEvent): Boolean =
        msgEvent.getText() == "确认" || msgEvent.getText() == "取消"

    override fun transition(msgEvent: MessageEvent): UserStage {
        if(!valid(msgEvent)){
            return this
        }
        if (msgEvent.getText() == "确认") {
            MsgSender.sendMsg(msgEvent, "已为您生成角色")
        } else {
            UserContext.getUser(msgEvent.sender.id).removeInfo(CddaUserInfo::class)
            MsgSender.sendMsg(msgEvent, "已取消")
        }
        return gameDefaultStage
    }

    override fun process(msgEvent: MessageEvent): UserStage {
        MsgSender.sendMsg(
            msgEvent, """
确认吗？
${msgEvent.getGameUserInfo()}
[确认]    [取消]
        """.trimIndent()
        )
        return this
    }

}


fun MessageEvent.getGameUserInfo(): CddaUserInfo =
    UserContext.getUser(this.sender.id).getInfo(CddaUserInfo::class) { CddaUserInfo(this.sender.id, this.senderName) }
