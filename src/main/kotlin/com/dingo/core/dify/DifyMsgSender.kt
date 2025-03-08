package com.dingo.core.dify

import com.dingo.config.AppConfig
import com.dingo.config.post
import com.dingo.config.properties.DifyProperty
import com.dingo.config.sendRequestThenGetResp
import com.fasterxml.jackson.annotation.JsonProperty
import okhttp3.Request


object DifyMsgSender {
    private val httpClient = AppConfig.okHttpClient

    private var conversationId: String = ""


    fun sendMsg(msg: String, userId: String): String {
        val host = DifyProperty.instance.url
        val port = DifyProperty.instance.port
        val appKey = DifyProperty.instance.appKey

        val body = DifyReq(msg, userId, conversationId)

        val difyResp = Request.Builder()
            .url("http://${host}:${port}/v1/chat-messages")
            .header("Authorization", "Bearer $appKey")
            .post(body).build()
            .sendRequestThenGetResp(DifyResp::class.java)

        conversationId = difyResp.conversationId

        return difyResp.answer
    }

}

data class DifyReq(
    val query: String,
    val user: String = "system",
    val conversation_id: String,
    val inputs: Map<String, Any> = HashMap(),

    @JsonProperty("response_mode")
    val responseMode: String = "blocking",  // 默认为阻塞模式
)

class DifyResp {
    @JsonProperty("message_id")
    var messageId: String = ""

    @JsonProperty("conversation_id")
    var conversationId: String = ""
    var answer: String = ""  // 完整回复内容
}