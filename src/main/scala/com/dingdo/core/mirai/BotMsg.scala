package com.dingdo.core.mirai

import net.mamoe.mirai.event.events.{GroupMessageEvent, MessageEvent}

import scala.language.implicitConversions

abstract sealed class BotMsg(val sourceMsg: MessageEvent) {
  val botId: Long = sourceMsg.getBot.getId
  val userId: Long = sourceMsg.getSender.getId
  val groupId: Long = sourceMsg match {
    case event: GroupMessageEvent => event.getGroup.getId
    case _ => -1
  }
}


class OneMsg(override val sourceMsg: MessageEvent) extends BotMsg(sourceMsg) {
  val content: String = sourceMsg.getMessage.contentToString()
}

object NoneMsg extends BotMsg(null)


object BotMsg {
  implicit def toOneMsg(messageEvent: MessageEvent): OneMsg = new OneMsg(messageEvent)
}
