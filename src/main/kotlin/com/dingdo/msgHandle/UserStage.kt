package com.dingdo.msgHandle

import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.PlainText
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component
import java.util.*
import kotlin.reflect.KClass

/**
 * 用户状态节点
 *
 * 用户的状态转移依靠以[UserStage]为基础的静态的有穷自动机完成，并且用户通过指令调度或其他方式实现自动机状态的转移。
 *
 * 如果需要该状态机生效，则需要将其交由spring容器管理
 *
 * 用户的起始状态为[DefaultRootStage]，从该节点开始构建自动机的映射网络，通过转移函数[transition]和处理函数[process]完成自动机状态的转移以及业务的处理。
 *
 * 用户节点有两种，一种为普通节点，即直接继承[UserStage]的节点。
 * 普通节点是该自动机业务逻辑处理的基本单元，所有的自动机内业务逻辑处理应当在普通节点中完成。
 * 普通节点需要显示申明一个上级节点，上级节点不一定是根节点，也可以是普通节点。
 * 如果指定的是根节点，表示该节点可以由指定根节点直接通过状态转移到达。
 * 如果指定的是普通节点，则暂无用处
 *
 * 另一种为根节点，即直接或间接继承[RootStage]的节点。
 * 根节点包含一些根节点的逻辑的通用实现。
 * 根节点的作用为普通节点提供中间层，以及普通节点提供通用转移实现，即根节点会根据排序顺序[order]
 * {若排序顺序相同则按节点名称排序}依次检查所有可直接转移的子节点，并跳转至第一个满足跳转条件[valid]的状态节点。
 * 若不存在满足条件的节点，则不会进行转移。
 * 所以根节点不宜用来实现业务逻辑，而是作为确定下一个用户状态的选择器
 *
 * @see StageHandler.msgHandle
 * @see RootStage
 */
interface UserStage {

    /**
     * 指定根节点类型
     */
    fun rootStage(): KClass<out UserStage>

    /**
     * 节点的检查优先级，数字越大优先级越高
     */
    fun order(): Int = 1

    /**
     * 节点的转移前提条件
     */
    fun valid(msgEvent: MessageEvent): Boolean

    /**
     * 节点转移函数，如果返回this则表示不进行转移
     */
    fun transition(msgEvent: MessageEvent): UserStage

    /**
     * 节点处理函数
     */
    fun process(msgEvent:MessageEvent):UserStage

    fun MessageEvent.getText(): String {
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
        return StageContext.rootStageMap.getOrElse(this::class) { emptyList() }
            .firstOrNull { it.valid(msgEvent) } ?: this
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
