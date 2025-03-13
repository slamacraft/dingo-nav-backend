package com.dingo.channel.service.qq

import com.dingo.channel.model.ChannelDto
import com.dingo.core.dify.DifyMsgSender
import com.dingo.core.qq.QQMsgSender
import com.dingo.module.entity.conversation.ConversationEntity
import com.dingo.module.entity.conversation.ConversationTable
import org.jetbrains.exposed.sql.selectAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * 用于处理向Dify AI发送的请求
 */
@Component
open class QQDifyService{
//    @Autowired
//    lateinit var qqMsgSender: QQMsgSender
    @Autowired
    lateinit var difyMsgSender: DifyMsgSender

    /**
     * 回复频道at消息
     */
    fun recallChannelAtMsg(dto: ChannelDto):String {
        val content = "@${dto.author.username} 对你说：${dto.content}"
        return sendChannelMsg(dto, content) {
            this["is_group_chat"] = "true"
        }
    }

    /**
     * 回复频道私聊消息
     */
    fun recallChannelPrivateMsg(dto: ChannelDto):String = sendChannelMsg(dto)

    /**
     * 回复频道里的消息
     */
    @Transactional
    open fun sendChannelMsg(
        dto: ChannelDto, content: String = dto.content,
        inputs: MutableMap<String, Any>.() -> Unit = {}
    ): String {
        // 通过channelId和guideId查询会话，如果存在则使用会话id发送消息
        val conversation = ConversationTable.selectAll()
            .where {
                ConversationTable.guideId eq dto.guild_id
                ConversationTable.channelId eq dto.channel_id
            }.firstOrNull()?.let {
                ConversationTable.buildEntity(it)
            }

        val userName = dto.author.username
        if (conversation != null) {
            // 如果有存在的会话，则直接使用会话id发送消息
            val (answer, _) = difyMsgSender.sendMsg(content, userName, conversation.conversationId, inputs)
            return answer
        }

        // 否则创建一个会话，并保存新的会话id
        val (answer, conversationId) = difyMsgSender.sendMsg(content, userName, "", inputs)
        ConversationTable.insert(ConversationEntity {
            this.channelId = dto.channel_id
            this.guideId = dto.guild_id
            this.conversationId = conversationId
        })
        return answer
    }
}