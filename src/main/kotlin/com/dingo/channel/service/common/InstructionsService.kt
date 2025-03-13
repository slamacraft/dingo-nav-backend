package com.dingo.channel.service.common

import com.dingo.channel.model.ChannelDto
import com.dingo.core.qq.QQMsgSender
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component

/**
 * 指令Service，用于处理以/开头的消息
 */
@Component
class InstructionsService : ApplicationContextAware {
    private val instructionMap = mutableMapOf<String, Instructions>()

    init {
        instance = this
    }

    companion object {
        lateinit var instance: InstructionsService
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        applicationContext.getBeansOfType(Instructions::class.java)
            .forEach { (_, bean) ->
                instructionMap.merge(bean.keyword(), bean) { _, _ ->
                    throw SecurityException("存在多个${bean.keyword()}指令服务")
                }
            }
    }

    fun recallChannelAtMsg(dto: ChannelDto): String {
        val instructionBean = getInstructionBean(dto.content)
        return instructionBean?.recallChannelPrivateMsg(dto) ?: "指令不完整，详情请查看 /帮助"
    }

    fun recallChannelPrivateMsg(dto: ChannelDto): String {
        val instructionBean = getInstructionBean(dto.content)
        return instructionBean?.recallChannelPrivateMsg(dto) ?: "指令不完整，详情请查看 /帮助"
    }

    fun getInstructionList(): List<String> {
        return instructionMap.values
            .map { "${it.keyword()}\t${it.desc()}" }
            .toList()
    }

    private fun getInstructionBean(content: String): Instructions? {
        val split = content.split("/")
        if (split.size < 2) {
            return null
        }
        return instructionMap[split[1].trim()]
    }


}

interface Instructions {
    /**
     * 指令关键字
     */
    fun keyword(): String

    /**
     * 指令描述
     */
    fun desc(): String
    fun recallChannelAtMsg(dto: ChannelDto):String
    fun recallChannelPrivateMsg(dto: ChannelDto):String
}