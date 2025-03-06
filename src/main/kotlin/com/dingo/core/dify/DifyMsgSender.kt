package com.dingo.core.dify

import cn.hutool.core.io.IoUtil
import com.dingo.config.properties.DifyProperty
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.serialization.Serializable
import okhttp3.OkHttpClient.Builder
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.time.Duration


object DifyMsgSender {
    private val httpClient = Builder()
        .connectTimeout(Duration.ofSeconds(100))
        .readTimeout(Duration.ofSeconds(100))
        .build()

    private var conversationId: String = ""
    private val objectMapper = ObjectMapper()

    init {
        //        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    fun sendMsg(msg: String, userId: Long):String {
        val host = DifyProperty.instance.url
        val port = DifyProperty.instance.port
        val appKey = DifyProperty.instance.appKey

        val body = objectMapper.writeValueAsString(DifyReq(msg, userId.toString(), conversationId))

        val req = Request.Builder()
            .url("http://${host}:${port}/v1/chat-messages")
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer $appKey")
            .post(body.toRequestBody())
            .build()

        val response = httpClient.newCall(req).execute()
        val bodyStr = IoUtil.read(response.body!!.byteStream(), Charsets.UTF_8)
        println(bodyStr)
        val difyResp =  objectMapper.readValue(bodyStr, DifyResp::class.java)
        conversationId = difyResp.conversationId

        return difyResp.answer
    }

}

@Serializable
data class DifyReq(
    val query: String,
    val user: String = "system",
    val conversation_id: String,
    val inputs: Map<String, Any> = HashMap(),

    @JsonProperty("response_mode")
    val responseMode: String = "blocking",  // 默认为阻塞模式
)

@Serializable
class DifyResp{
    @JsonProperty("message_id")
    var messageId: String = ""
    @JsonProperty("conversation_id")
    var conversationId: String = ""
    var answer: String = ""  // 完整回复内容
}