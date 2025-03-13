package com.dingo.channel.service.oneBot

import com.dingo.channel.model.*
import com.dingo.core.dify.DifyMsgSender
import com.dingo.module.entity.conversation.ConversationEntity
import com.dingo.module.entity.conversation.ConversationTable
import org.jetbrains.exposed.sql.selectAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
open class OneBotMsgService {
    @Autowired
    lateinit var difyMsgSender: DifyMsgSender

    @Transactional
    open fun handlePrivateMsg(dto: PrivateMsgDto): BaseMessageVo {
        val conversation = getConversation(dto.user_id, 0L)
        val content = dto.message.get<Text>().joinToString { it.text }
        val answer = difyMsgSender.sendMsg(
            content,
            dto.sender.nickname,
            conversation?.conversationId ?: ""
        )
        if (conversation == null) {
            setConversation(dto.user_id, 0L, answer.conversationId)
        }
        val result = PrivateMessageVo()
        val text = Text()
        text.text = answer.content
        result.reply = listOf(MsgVo(text))
        return result
    }

    @Transactional
    open fun handleGroupMsg(dto: GroupMsgDto): BaseMessageVo {
        val conversation = getConversation(dto.user_id, dto.group_id)
        // 判断有没有at机器人
        val isAtBot = dto.message.get<At>().firstOrNull {
            it.qq == dto.self_id.toString()
        }
        if (isAtBot == null) {
            return GroupMessageVo()
        }

        val content = dto.message.get<Text>().joinToString { it.text }
        val answer = difyMsgSender.sendMsg(
            content,
            dto.sender.nickname,
            conversation?.conversationId ?: ""
        )
        if (conversation == null) {
            setConversation(dto.user_id, dto.group_id, answer.conversationId)
        }
        val result = GroupMessageVo()
        val text = Text()
        text.text = answer.content
        result.reply = listOf(MsgVo(text))
        result.at_sender = true
        return result
    }

    private fun getConversation(userId: Long, groupId: Long): ConversationEntity? {
        return ConversationTable.selectAll()
            .where {
                ConversationTable.userId eq userId
                ConversationTable.groupId eq groupId
            }.firstOrNull()?.let {
                ConversationTable.buildEntity(it)
            }
    }

    private fun setConversation(userId: Long, groupId: Long, conversationId: String) {
        ConversationTable.insert(ConversationEntity {
            this.userId = userId
            this.groupId = groupId
            this.conversationId = conversationId
        })
    }

}