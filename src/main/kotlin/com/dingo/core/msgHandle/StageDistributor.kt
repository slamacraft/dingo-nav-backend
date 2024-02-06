package com.dingo.core.msgHandle

import com.dingo.context.UserContext
import com.dingo.core.dfa.UserStage
import com.dingo.core.mirai.BotInitializer
import com.dingo.enums.UserStageEnum
import jakarta.annotation.PostConstruct
import net.mamoe.mirai.event.events.MessageEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class StageDistributor {

    @Autowired
    lateinit var userContext: UserContext

    @PostConstruct
    fun registerMiraiEvent() {
        BotInitializer.registeredGroupMsgEvent { msgHandle(it) }
        BotInitializer.registeredFriendMsgEvent { msgHandle(it) }
    }

    private fun msgHandle(msgEvent: MessageEvent) {
        val user = userContext.getUser(msgEvent.sender.id)
        val info = user.getInfo(UserStage::class) { UserStage(UserStageEnum.DEFAULT) }
        user.registerInfo(info.nextStage(msgEvent))
    }
}
