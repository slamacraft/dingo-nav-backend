package com.dingdo.model.entity

import net.mamoe.mirai.event.events.{GroupMessageEvent, MessageEvent}
import org.springframework.data.annotation.{CreatedDate, Id}
import org.springframework.data.mongodb.core.mapping.Document

import java.time.LocalDateTime
import java.util.stream.Collectors


@Document("bot_msg")
class BotMsgEntity {
  @Id
  var id: Long = _
  var botId: Long = _
  var userId: Long = _
  var userName: String = _
  var groupId: Long = _
  var groupName: String = _
  var content: String = _
  @CreatedDate
  var createTime: LocalDateTime = _
}

object BotMsgEntity{
  def build(event: MessageEvent): BotMsgEntity ={
    val msgContent = event.getMessage.stream()
      .map[String](_.contentToString())
      .collect(Collectors.joining())
    val result = new BotMsgEntity()
    result.botId = event.getBot.getId
    result.userId = event.getSender.getId
    result.userName = event.getSender.getNick
    val (groupId, groupName) = event match {
      case e: GroupMessageEvent => (e.getGroup.getId, e.getGroup.getName)
      case _ => (-1L, "")
    }
    result.groupId = groupId
    result.groupName = groupName
    result.content = msgContent
    result
  }
}