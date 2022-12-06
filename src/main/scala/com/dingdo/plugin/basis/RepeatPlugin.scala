package com.dingdo.plugin.basis

import com.dingdo.core.BotPlugin
import com.dingdo.core.mirai.BotManager._
import com.dingdo.core.mirai.{BotMsg, OneMsg}
import net.mamoe.mirai.event.events.{GroupMessageEvent, MessageEvent}

import scala.collection.mutable

// 复读要去掉已复读过的句子
class RepeatPlugin extends BotPlugin {
  override val name: String = "复读"

  // 重复次数
  private val repeatCount = 3
  // 这个群上次复读得语句，如果这次复读得和上次得相同，则不复读
  private val groupRepeat = new mutable.HashMap[Long, String]()

  def repeat(msg: MessageEvent): Unit = {
    msg match {
      case groupEvent: GroupMessageEvent => repeatGroupMsg(groupEvent)
      case _ =>
    }
  }

  private def repeatGroupMsg(groupEvent: GroupMessageEvent): Unit = {
    groupEvent.getBot
    val msg = groupEvent.getMessage.contentToString
    val groupId = groupEvent.getGroup.getId

    val groupMsg = groupEvent.getBot.msgCache.group(groupId)
    val toRepeat = groupMsg.msg.slice(1, repeatCount)
      .forall(it => it.msg.endsWith(msg) && groupRepeat(groupId) != msg)

    if (toRepeat) {
      groupEvent.getGroup.sendMessage(msg)
      groupRepeat(groupId) = msg
    }
  }

  /**
   * 插件会如何处理单条消息
   */
  override def apply(msg: OneMsg): BotMsg = ???
}
