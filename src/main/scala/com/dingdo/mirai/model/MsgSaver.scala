package com.dingdo.mirai.model

import com.dingdo.config.configuration.SlickConfig
import com.dingdo.mirai.core.MsgHandler
import net.mamoe.mirai.event.events.{GroupMessageEvent, MessageEvent}

import java.util.stream.Collectors
import slick.jdbc.MySQLProfile.api._

object MsgSaver extends MsgHandler {

  def saveMsg(event: MessageEvent): Unit = {
    val msgContent = event.getMessage.stream()
      .map[String](_.contentToString())
      .collect(Collectors.joining())

    val (groupId, groupName) = event match {
      case e: GroupMessageEvent => (e.getGroup.getId, e.getGroup.getName)
      case _ => (-1L, "")
    }

    val user = event.getSender
    val msgEntity = BotMsg.MsgEntity(userId = user.getId, userName = user.getNick,
      groupId = groupId, groupName = groupName, content = msgContent)

    SlickConfig.DB.run(BotMsg.msg += msgEntity)
  }

}
