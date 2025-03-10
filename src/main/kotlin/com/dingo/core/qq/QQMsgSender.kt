package com.dingo.core.qq

import com.dingo.common.expand.sendRequest
import com.dingo.config.properties.QQProperty
import com.dingo.core.qq.util.postRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Component
open class QQMsgSender {
    @Autowired
    lateinit var qqProperty: QQProperty

    init {
        instance = this
    }

    companion object{
        lateinit var instance: QQMsgSender
    }


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
        "/channels/${channelId}/messages".postRequest(body).sendRequest()
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
        "/dms/${guildId}/messages".postRequest(body).sendRequest()
    }
}

