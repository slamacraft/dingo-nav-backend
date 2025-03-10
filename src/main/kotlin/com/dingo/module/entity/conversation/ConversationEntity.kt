package com.dingo.module.entity.conversation

import com.dingo.module.base.BaseEntity
import com.dingo.module.base.BaseTable
import com.dingo.module.base.Entity


/**
 * 对话表
 */
interface ConversationEntity : Entity<ConversationEntity>, BaseEntity {
    companion object : Entity.Factory<ConversationEntity>()

    var channelId: String? // 子频道id
    var guideId: String? // 频道私聊id
    var conversationId: String  // 对应的Dify对话id
}


object ConversationTable : BaseTable<ConversationEntity>("bot_conversation") {
    val channelId = varchar("channel_id", 128)
    val guideId = varchar("guide_id", 128)
    val conversationId = varchar("conversation_id", 128).default("")
}