package com.dingo.core

import net.mamoe.mirai.event.events.GroupMessageEvent

sealed abstract class BotMsg {
}

class OneMsg(val msgEvent: GroupMessageEvent) extends BotMsg {
  val botId = msgEvent.getBot.getId
  val userId = msgEvent.getSender.getId
  val groupId = msgEvent.getGroup.getId
  val content = msgEvent.getMessage.contentToString()
}

object NoneMsg extends BotMsg

case class MsgContent(userId: Long, content: String)