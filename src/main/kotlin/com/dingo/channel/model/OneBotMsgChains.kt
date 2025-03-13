package com.dingo.channel.model

import com.dingo.common.expand.caseTo
import com.fasterxml.jackson.databind.JsonNode

class MsgChains(
    val msgList: MutableList<IOneBotMsg> = mutableListOf()
) : List<IOneBotMsg> by msgList {

    companion object {
        operator fun invoke(jsonNode: JsonNode): MsgChains {
            if (!jsonNode.isArray) {
                throw RuntimeException("只支持array类型的message")
            }
            val msgList = jsonNode.map { msg ->
                val type = msg.path("type").asText()
                val dataNode = msg.path("data")
                when (type) {
                    "text" -> dataNode.caseTo(Text::class)
                    "face" -> dataNode.caseTo(Face::class)
                    "image" -> dataNode.caseTo(Image::class)
                    "record" -> dataNode.caseTo(Record::class)
                    "video" -> dataNode.caseTo(Video::class)
                    "at" -> dataNode.caseTo(At::class)
                    "rps" -> dataNode.caseTo(Rps::class)
                    "dice" -> dataNode.caseTo(Dice::class)
                    "shake" -> dataNode.caseTo(Shake::class)
                    "poke" -> dataNode.caseTo(Poke::class)
                    "share" -> dataNode.caseTo(Share::class)
                    else -> null
                }
            }.filterNotNull().toMutableList()

            return MsgChains(msgList)
        }
    }

    operator fun plus(other: IOneBotMsg): MsgChains {
        this.msgList.add(other)
        return this
    }

    operator fun plusAssign(other: IOneBotMsg) {
        msgList.add(other)
    }

    inline fun <reified T : IOneBotMsg> get(): List<T> {
        return msgList.filterIsInstance<T>()
    }

}

interface IOneBotMsg {
    fun msgType(): String

    fun toMsgChains(): MsgChains {
        return MsgChains(mutableListOf(this))
    }

    operator fun plus(other: IOneBotMsg): MsgChains {
        return MsgChains(mutableListOf(this, other))
    }
}

class Text : IOneBotMsg {
    override fun msgType() = "text"
    var text: String = ""   // 纯文本内容
}

class Face : IOneBotMsg {
    override fun msgType() = "face"
    var id: Int =
        0   // 表情id，详情：https://github.com/richardchien/coolq-http-api/wiki/%E8%A1%A8%E6%83%85-CQ-%E7%A0%81-ID-%E8%A1%A8
}

class Image : IOneBotMsg {
    override fun msgType() = "image"

    /**
     * [1] 发送时，file 参数除了支持使用收到的图片文件名直接发送外，还支持：
     *
     * 绝对路径，例如 file:///C:\\Users\Richard\Pictures\1.png，格式使用 file URI
     * 网络 URL，例如 http://i1.piimg.com/567571/fdd6e7b6d93f1ef0.jpg
     * Base64 编码，例如 base64://iVBORw0KGgoAAAANSUhEUgAAABQAAAAVCAIAAADJt1n/AAAAKElEQVQ4EWPk5+RmIBcwkasRpG9UM4mhNxpgowFGMARGEwnBIEJVAAAdBgBNAZf+QAAAAABJRU5ErkJggg==
     */
    var file: String = ""   // 图片文件名
    var type: String? = null    // 图片类型，flash 表示闪照，无此参数表示普通图片
    var url: String = ""   // 图片链接，CQ 码中的 url 参数
}

class Record : IOneBotMsg {
    override fun msgType() = "record"

    /**
     * 发送时，file 参数除了支持使用收到的语音文件名直接发送外，还支持其它形式，参考 图片。
     */
    var file: String = ""   // 语音文件名
    var magic: Int = 0   // 是否为变声，0 表示正常语音，1 表示变声语音，无此参数表示普通语音
    var url: String = ""   // 语音链接，CQ 码中的 url 参数
}

class Video : IOneBotMsg {
    override fun msgType() = "video"

    /**
     * [1] 发送时，file 参数除了支持使用收到的视频文件名直接发送外，还支持其它形式，参考 图片。
     */
    var file: String = ""   // 视频文件名
    var url: String = ""   // 视频 URL
}

class At : IOneBotMsg {
    override fun msgType() = "at"
    var qq: String = ""  // QQ 号、all	@的 QQ 号，all 表示全体成员
}

class Rps : IOneBotMsg {
    override fun msgType() = "rps"
}

class Dice : IOneBotMsg {
    override fun msgType() = "dice"
}

class Shake : IOneBotMsg {
    override fun msgType() = "shake"
}

class Poke : IOneBotMsg {
    override fun msgType() = "poke"
    var type: String = ""   // 见 Mirai 的 PokeMessage 类	类型
    var id: String = ""
}

class Share : IOneBotMsg {
    override fun msgType() = "share"
    var url: String = ""   // 链接
    var title: String = ""   // 标题
    var content: String = ""   // 内容
    var image: String = ""   // 图片 URL
}