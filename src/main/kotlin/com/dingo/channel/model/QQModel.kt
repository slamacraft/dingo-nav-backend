package com.dingo.channel.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

class QQModel {
    lateinit var id: String  // 事件id
    var op: Long = 0 // 指的是 opcode，参考连接维护
    var s: Long = 0  // 下行消息都会有一个序列号，标识消息的唯一性，客户端需要再发送心跳的时候，携带客户端收到的最新的s
    lateinit var t: String   // 代表事件类型。主要用在op为 0 Dispatch 的时候
    lateinit var d: Map<String, Any?>
}

class VerifyDto {
    lateinit var plain_token: String
    lateinit var event_ts: String
}

data class VerifyVo(
    var plain_token: String,
    var signature: String
)

class Author {
    lateinit var avatar: String
    var bot: Boolean = false
    lateinit var id: String
    lateinit var username: String
}

class ChannelDto {
    lateinit var author: Author
    lateinit var channel_id: String
    lateinit var content: String
    lateinit var guild_id: String
    lateinit var id: String
    lateinit var timestamp: String
}