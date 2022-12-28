package com.dingo.plugin.basis.model.entity

import net.mamoe.mirai.event.events.{GroupMessageEvent, MessageEvent}
import org.springframework.data.annotation.{CreatedDate, Id}
import org.springframework.data.mongodb.core.mapping.Document

import java.time.LocalDateTime
import java.util.UUID
import java.util.stream.Collectors


@Document("bot_msg")
class BotMsgEntity {
  @Id
  var id: Long = UUID.randomUUID().getLeastSignificantBits
  var name: String = _
  var botId: Long = _
  var groupId: Long = _
  var userId: Long = _
  var userName: String = _
  var groupName: String = _
  var content: String = _
  @CreatedDate
  var createTime: LocalDateTime = _
}

object BotMsgEntity {
  def build(event: GroupMessageEvent): BotMsgEntity = {
    val msgContent = event.getMessage.stream()
      .map[String](_.contentToString())
      .collect(Collectors.joining())
    val result = new BotMsgEntity()
    result.botId = event.getBot.getId
    result.userId = event.getSender.getId
    result.userName = event.getSender.getNick
    result.groupId = event.getGroup.getId
    result.groupName = event.getGroup.getName
    result.content = msgContent
    result
  }
}