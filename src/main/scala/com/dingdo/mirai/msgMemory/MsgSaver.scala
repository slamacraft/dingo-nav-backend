package com.dingdo.mirai.msgMemory

import com.dingdo.mirai.MiraiBot
import com.dingdo.mirai.core.MsgHandler
import net.mamoe.mirai.event.events.{GroupMessageEvent, MessageEvent}

import java.util.stream.{Collector, Collectors}

object MsgSaver extends MsgHandler {

  def saveMsg(event: MessageEvent): Unit = {
    val msgContent = event.getMessage.stream()
      .map(it => it.contentToString())
      .collect(Collectors.joining())

    val groupInfo = event match {
      case e:GroupMessageEvent => (e.getGroup.getId, e.getGroup.getName)
      case _ => (_, _)
    }

    BotMsg.MsgEntity(1, event.getSender.getId, groupInfo)
  }

}
