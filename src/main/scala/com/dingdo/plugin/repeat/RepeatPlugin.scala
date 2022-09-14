package com.dingdo.plugin.repeat

import net.mamoe.mirai.event.events.{GroupMessageEvent, MessageEvent}

class RepeatPlugin {

  def repeat(msg: MessageEvent): Unit = {
    msg match {
      case groupEvent: GroupMessageEvent => {

      }
      case _ =>
    }

  }

  def repeat(groupEvent:GroupMessageEvent): Unit ={
    groupEvent.getMessage
  }

}
