package com.dingo.core.dify.model

import com.fasterxml.jackson.annotation.JsonProperty


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