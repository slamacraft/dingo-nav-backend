package com.dingo.channel.model

abstract class BaseMessageVo {
    var reply: List<MsgVo>? = null
}

data class MsgVo(
    var type: String,
    var data: IOneBotMsg
) {
    companion object {
        operator fun invoke(msgChains: MsgChains?): List<MsgVo>? {
            if (msgChains == null) {
                return null
            }
            return msgChains.map {
                MsgVo(it.msgType(), it)
            }
        }

        operator fun invoke(oneBotMsg: IOneBotMsg): MsgVo {
            return MsgVo(oneBotMsg.msgType(), oneBotMsg)
        }
    }
}

// 新增 PrivateMessageVo 类
class PrivateMessageVo : BaseMessageVo() {
    var auto_escape: Boolean = false // 消息内容是否作为纯文本发送（即不解析 CQ 码），只在 reply 字段是字符串时有效
}

// 新增 GroupMessageVo 类
class GroupMessageVo : BaseMessageVo() {
    var auto_escape: Boolean = false // 消息内容是否作为纯文本发送（即不解析 CQ 码），只在 reply 字段是字符串时有效
    var at_sender: Boolean = true // 是否要在回复开头 at 发送者（自动添加），发送者是匿名用户时无效
    var delete: Boolean = false // 撤回该条消息
    var kick: Boolean = false // 把发送者踢出群组（需要登录号权限足够），不拒绝此人后续加群请求，发送者是匿名用户时无效
    var ban: Boolean = false // 把发送者禁言 ban_duration 指定时长，对匿名用户也有效
    var ban_duration: Int = 30 // 禁言时长
}