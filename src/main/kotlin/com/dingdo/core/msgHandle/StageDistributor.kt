package com.dingdo.core.msgHandle

import com.dingdo.core.robot.mirai.MiraiRobotInitializer
import com.dingdo.core.context.UserContext
import com.dingdo.core.dfa.UserStage
import com.dingdo.enums.UserStageEnum
import net.mamoe.mirai.event.events.MessageEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class StageDistributor {

    @Autowired
    lateinit var userContext: UserContext

    @PostConstruct
    fun registerMiraiEvent() {
        MiraiRobotInitializer.registeredGroupMsgEvent { msgHandle(it) }
        MiraiRobotInitializer.registeredFriendMsgEvent { msgHandle(it) }
    }

    private fun msgHandle(msgEvent: MessageEvent) {
        val user = userContext.getUser(msgEvent.sender.id)
        val info = user.getInfo(UserStage::class) { UserStage(UserStageEnum.DEFAULT) }
        user.registerInfo(info.nextStage(msgEvent))
    }
}
