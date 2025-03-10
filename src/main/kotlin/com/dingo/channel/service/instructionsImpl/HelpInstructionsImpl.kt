package com.dingo.channel.service.instructionsImpl

import com.dingo.channel.model.ChannelDto
import com.dingo.channel.service.Instructions
import com.dingo.channel.service.InstructionsService
import com.dingo.core.qq.QQMsgSender
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
open class HelpInstructionsImpl: Instructions {
    @Autowired
    lateinit var qqMsgSender: QQMsgSender

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


    override fun recallChannelAtMsg(dto: ChannelDto) {
        qqMsgSender.recallChannelAtMsg(
            dto.channel_id,
            dto.id,
            getRecallMsg()
        )
    }

    override fun recallChannelPrivateMsg(dto: ChannelDto) {
        qqMsgSender.recallChannelPrivateMsg(
            dto.guild_id,
            dto.id,
            getRecallMsg()
        )
    }
}