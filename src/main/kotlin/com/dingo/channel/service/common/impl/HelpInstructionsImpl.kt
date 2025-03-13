package com.dingo.channel.service.common.impl

import com.dingo.channel.model.ChannelDto
import com.dingo.channel.service.common.Instructions
import com.dingo.channel.service.common.InstructionsService
import com.dingo.core.qq.QQMsgSender
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
open class HelpInstructionsImpl : Instructions {

    override fun keyword(): String = "帮助"

    override fun desc(): String = "查看帮助文档"

    private fun getRecallMsg(): String {
        val constructionList = InstructionsService.instance.getInstructionList()
            .joinToString("\n") { "/${it}" }
        return """
指令是以/开头的字符，拥有特定功能。
目前支持的指令有：
$constructionList
        """.trimIndent()
    }


    override fun recallChannelAtMsg(dto: ChannelDto): String = getRecallMsg()

    override fun recallChannelPrivateMsg(dto: ChannelDto): String = getRecallMsg()
}