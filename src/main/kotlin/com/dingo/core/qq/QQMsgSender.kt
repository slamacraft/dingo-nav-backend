package com.dingo.core.qq

import com.dingo.config.post
import com.dingo.config.sendRequest
import okhttp3.Request
import org.springframework.stereotype.Component

/**
 * 发送消息的基础地址
 */
private const val baseUrl = "https://api.sgroup.qq.com"

@Component
open class QQMsgSender {

    /**
     * 发送频道at消息
     * @param channelId 频道id
     * @param msgId 消息id，用于回复消息，如果不传这个会视为主动发送消息，每天发送的消息数量就会有限制
     * @param content 消息内容
     */
    fun recallChannelAtMsg(
        channelId: String,
        msgId: String,
        content: String
    ) {
        val body = mapOf(
            "content" to content,
            "msg_id" to msgId,
            "message_reference" to mapOf(
                "message_id" to msgId
            )
        )
        "/channels/${channelId}/messages".buildRequest(body).sendRequest()
    }

    /**
     * 发送频道私聊消息
     * @param guildId 私聊窗口id
     * @param msgId 消息id，用于回复消息，如果不传这个会视为主动发送消息，每天发送的消息数量就会有限制
     * @param content 消息内容
     */
    fun recallChannelPrivateMsg(
        guildId: String,
        msgId: String,
        content: String
    ) {
        val body = mapOf(
            "content" to content,
            "msg_id" to msgId,
            "message_reference" to mapOf(
                "message_id" to msgId
            )
        )
        "/dms/${guildId}/messages".buildRequest(body).sendRequest()
    }
}

/**
 * 将String视为一个url地址构建请求
 */
fun String.buildRequest(body: Any): Request {
    return Request.Builder()
        .url("$baseUrl$this")
        .addHeader("Authorization", "QQBot ${QQAuthHandler.instance.getAccessToken()}")
        .post(body)
        .build()
}