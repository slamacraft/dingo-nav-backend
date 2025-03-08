package com.dingo.core.qq

import cn.hutool.core.io.IoUtil
import com.dingo.config.post
import com.dingo.config.sendRequest
import okhttp3.Request

private const val baseUrl = "https://api.sgroup.qq.com"

object QQMsgSender {

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
        val response = "/dms/${guildId}/messages".buildRequest(body).sendRequest()
        val bodyStr = IoUtil.read(response.body!!.byteStream(), Charsets.UTF_8)
        println(bodyStr)
    }
}

class

private

fun String.buildRequest(body: Any): Request {
    return Request.Builder()
        .url("$baseUrl$this")
        .addHeader("Authorization", "QQBot ${QQAuthHandler.instance.getAccessToken()}")
        .post(body)
        .build()
}