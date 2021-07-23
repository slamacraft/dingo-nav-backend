package com.dingdo.msgHandle.stage

import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.PlainText
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component
import java.util.*
import kotlin.reflect.KClass

interface UserStage {

    fun rootStage(): KClass<out UserStage>

    fun order(): Int {
        return 1
    }

    fun valid(msgEvent: MessageEvent): Boolean

    fun transition(msgEvent: MessageEvent): UserStage

    fun process(msgEvent:MessageEvent):UserStage

    fun MessageEvent.getMsgText(): String {
        return this.message.filterIsInstance<PlainText>()
            .map { it.content }
            .reduce { acc, s -> acc.plus(s) }
    }
}

interface RootStage : UserStage {

    override  fun rootStage(): KClass<out UserStage>  = DefaultRootStage::class

    override fun process(msgEvent: MessageEvent): UserStage {
        return this
    }

    override fun transition(msgEvent: MessageEvent): UserStage {
        val subStageList = StageContext.rootStageMap.getOrElse(this::class) { emptyList() }
        for (stage in subStageList) {
            if (stage.valid(msgEvent)) {
                return stage
            }
        }
        return this
    }
}


@Component
object DefaultRootStage : RootStage, ApplicationContextAware {

    var defaultStage: UserStage = this

    private val stageLinkList = LinkedList<UserStage>()

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        val userStageMap = applicationContext.getBeansOfType(UserStage::class.java)
        userStageMap.values
            .filter { Objects.equals(it.rootStage(), defaultStage) && !Objects.equals(it, defaultStage) }
            .sortedWith(compareBy({ it.order() }, { it::class.simpleName }))
            .also { stageLinkList.addAll(it) }
        defaultStage = this
    }

    override fun valid(msgEvent: MessageEvent): Boolean {
        return true
    }
}
