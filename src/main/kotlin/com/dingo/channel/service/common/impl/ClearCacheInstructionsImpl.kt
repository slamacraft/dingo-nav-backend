package com.dingo.channel.service.common.impl

import com.dingo.channel.model.ChannelDto
import com.dingo.channel.service.common.Instructions
import com.dingo.core.qq.QQMsgSender
import com.dingo.core.qq.QQUserInfoGetter
import com.dingo.module.entity.conversation.ConversationTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * 清理聊天缓存
 */
@Component
open class ClearCacheInstructionsImpl : Instructions {
    @Autowired
    lateinit var qqUserInfoGetter: QQUserInfoGetter

    @Autowired
    lateinit var qqMsgSender: QQMsgSender

    override fun keyword(): String = "重新开始"

    override fun desc(): String = "清除上下文"

    override fun recallChannelAtMsg(dto: ChannelDto): String {
        val userInfo = qqUserInfoGetter.getChannelUserInfo(dto.channel_id, dto.author.id)
        if (!userInfo.roles.contains("超级管理员")) {
            return "权限不足"
        }
        return "重新开始聊天"
    }

    @Transactional
    override fun recallChannelPrivateMsg(dto: ChannelDto): String {
        // 删除缓存
        ConversationTable.deleteWhere {
            channelId eq dto.channel_id
            guideId eq dto.author.id
        }
        return "重新开始聊天"
    }
}