package com.dingo.channel.model

import com.dingo.common.ObjectMappers
import com.dingo.common.expand.caseTo
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.JsonNode
import kotlin.reflect.KClass

abstract class BaseOneDto {
    var time: Long = 0L // 事件发生的时间戳
    var self_id: Long = 0L   // 收到事件的机器人 QQ 号

    /**
     * 事件类型
     * message：消息事件
     * notice：通知事件
     * request：请求事件
     * meta_event：元事件
     */
    lateinit var post_type: String
}

abstract class BaseMsgDto : BaseOneDto() {
    var message_type: String = "" // 消息类型，固定为 group 或 private
    var message_id: Int = 0 // 消息 ID
    var user_id: Long = 0L // 发送者 QQ 号

    @JsonIgnore
    lateinit var message: MsgChains // 消息内容
    var raw_message: String = "" // 原始消息内容

    /**
     * 消息子类型，
     * 私聊：friend（好友）、group（群临时会话）、other
     * 群聊：normal（正常消息）、anonymous（匿名消息）、notice（系统提示）
     */
    var sub_type: String = ""
    var font: Int = 0 // 字体
    var sender: Sender = Sender() // 发送人信息，可能为空

    companion object {
        fun <T : BaseMsgDto> JsonNode.toDto(clazz: KClass<T>): T {
            val result = ObjectMappers.jsonMapper.readValue(this.toString(), clazz.java)
            val msgNode = this["message"]
            result.message = MsgChains(msgNode)
            return result
        }
    }
}

// 新增私聊消息类型
class PrivateMsgDto : BaseMsgDto()

// 新增 GroupMessage 类以表示群消息类型
class GroupMsgDto : BaseMsgDto() {
    var group_id: Long = 0L
    var anonymous: Anonymous? = null // 匿名信息，如果不是匿名消息则为 null
}

// 新增 Anonymous 类以表示匿名用户信息
class Anonymous {
    var id: Long = 0L // 匿名用户 ID
    var name: String = "" // 匿名用户名称
    var flag: String = "" // 匿名用户 flag，在调用禁言 API 时需要传入
}

// 扩展 Sender 类以包含群消息中的额外字段
class Sender {
    var user_id: Long = 0L // 发送者 QQ 号
    var nickname: String = "" // 昵称
    var card: String = "" // 群名片／备注
    var sex: String = "" // 性别，male 或 female 或 unknown
    var age: Int = 0 // 年龄
    var area: String = "" // 地区
    var level: String = "" // 成员等级
    var role: String = "" // 角色，owner 或 admin 或 member
    var title: String = "" // 专属头衔
}



