package com.dingo.core.dify

import com.dingo.common.expand.post
import com.dingo.common.expand.sendRequestThenGetResp
import com.dingo.config.properties.DifyProperty
import com.dingo.core.dify.model.Answer
import com.dingo.core.dify.model.DifyResp
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
            .sendRequestThenGetResp(DifyResp::class)
        return Answer(
            difyResp.answer,
            difyResp.conversationId
        )
    }

}
