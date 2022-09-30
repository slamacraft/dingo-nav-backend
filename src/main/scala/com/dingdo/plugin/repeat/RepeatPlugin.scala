package com.dingdo.plugin.repeat

import com.dingdo.mirai.context.MsgCacheContext
import com.dingdo.mirai.core.BotPlugin
import net.mamoe.mirai.event.events.{GroupMessageEvent, MessageEvent}

class RepeatPlugin{

  // todo 重复次数
  val repeatCount = 3

  def repeat(msg: MessageEvent): Unit = {
    msg match {
      case groupEvent: GroupMessageEvent => {
        val msg = groupEvent.getMessage.contentToString()
        val groupMsg = MsgCacheContext.group(groupEvent.getGroup.getId)
        val toRepeat = groupMsg.msg.slice(1, repeatCount)
          .forall(it => it.msg.endsWith(msg))

        // todo 进行复读
        if(toRepeat){
          groupEvent.getGroup.sendMessage(msg)
        }
      }
      case _ =>
    }

  }
}
