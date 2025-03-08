package com.dingo.core.dify

import com.dingo.config.post
import com.dingo.config.properties.DifyProperty
import com.dingo.config.sendRequestThenGetResp
import com.fasterxml.jackson.annotation.JsonProperty
import okhttp3.Request
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Component
open class DifyMsgSender {
    @Autowired
    lateinit var difyProperty: DifyProperty

    /**
     * 向dify的ai发送消息
     *
     * @param content 消息内容
     * @param userName 用户名
     * @param conversationId 会话id
     * @param inputs 输入参数，详情请参见Dify的文档
     */
    fun sendMsg(
        content: String, userName: String, conversationId: String,
        inputs: MutableMap<String, Any>.() -> Unit = {}
    ): Answer {
        val host = difyProperty.url
        val port = difyProperty.port
        val appKey = difyProperty.appKey

        val inputs = mutableMapOf<String, Any>().apply(inputs)

        val body = mapOf(
            "query" to content,
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