package com.dingo.core.dify

import com.dingo.config.post
import com.dingo.config.properties.DifyProperty
import com.dingo.config.sendRequestThenGetResp
import com.dingo.module.entity.conversation.ConversationEntity
import com.dingo.module.entity.conversation.ConversationTable
import com.fasterxml.jackson.annotation.JsonProperty
import okhttp3.Request
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional


@Component
open class DifyMsgSender {
    @Autowired
    lateinit var difyProperty: DifyProperty

    @Transactional
    open fun sendChannelMsg(
        msg: String, userName: String, channelId: String, guideId: String,
        inputs: MutableMap<String, Any>.() -> Unit = {}
    ): String {
        // 通过channelId和guideId查询会话，如果存在则使用会话id发送消息
        val conversation = ConversationTable.selectAll()
            .where {
                (ConversationTable.guideId eq guideId) and
                        (ConversationTable.channelId eq channelId)
            }.firstOrNull()?.let {
                ConversationTable.buildEntity(it)
            }

        if (conversation != null) {
            // 如果有存在的会话，则直接使用会话id发送消息
            val (answer, _) = sendMsg(msg, userName, conversation.conversationId, inputs)
            return answer
        }

        // 否则创建一个会话，并保存新的会话id
        val (answer, conversationId) = sendMsg(msg, userName, "", inputs)
        ConversationTable.insert(ConversationEntity {
            this.channelId = channelId
            this.guideId = guideId
            this.conversationId = conversationId
        })
        return answer
    }

    /**
     * 向dify的ai发送消息
     */
    private fun sendMsg(
        msg: String, userName: String, conversationId: String,
        inputs: MutableMap<String, Any>.() -> Unit = {}
    ): Answer {
        val host = difyProperty.url
        val port = difyProperty.port
        val appKey = difyProperty.appKey

        val inputs = mutableMapOf<String, Any>().apply(inputs)

        val body = mapOf(
            "query" to msg,
            "user" to userName,
            "conversation_id" to conversationId,
            "response_mode" to "blocking",
            "inputs" to inputs
        )

        val difyResp = Request.Builder()
            .url("http://${host}:${port}/v1/chat-messages")
            .header("Authorization", "Bearer $appKey")
            .post(body).build()
            .sendRequestThenGetResp(DifyResp::class.java)
        return Answer(
            difyResp.answer,
            difyResp.conversationId
        )
    }

}

class DifyResp {
    @JsonProperty("message_id")
    var messageId: String = ""

    @JsonProperty("conversation_id")
    var conversationId: String = ""
    var answer: String = ""  // 完整回复内容
}

data class Answer(
    val content: String,
    val conversationId: String,
)