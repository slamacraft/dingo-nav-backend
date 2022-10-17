package com.dingdo.plugin.repeat

import com.dingdo.mirai.context.MsgCacheContext
import com.dingdo.mirai.core.{BotPlugin, ParallelBotPlugin}
import net.mamoe.mirai.event.events.{GroupMessageEvent, MessageEvent}

import scala.collection.mutable

// 复读要去掉已复读过的句子
class RepeatPlugin extends ParallelBotPlugin{

  // 重复次数
  val repeatCount = 3
  // 这个群上次复读得语句，如果这次复读得和上次得相同，则不复读
  val groupRepeat = new mutable.HashMap[Long, String]()

  def repeat(msg: MessageEvent): Unit = {
    msg match {
      case groupEvent: GroupMessageEvent => repeatGroupMsg(groupEvent)
      case _ =>
    }
  }

  def repeatGroupMsg(groupEvent: GroupMessageEvent): Unit = {
    val msg = groupEvent.getMessage.contentToString
    val groupId = groupEvent.getGroup.getId

    val groupMsg = MsgCacheContext.group(groupId)
    val toRepeat = groupMsg.msg.slice(1, repeatCount)
      .forall(it => it.msg.endsWith(msg) && groupRepeat(groupId) != msg)

    if (toRepeat) {
      groupEvent.getGroup.sendMessage(msg)
      groupRepeat(groupId) = msg
    }
  }

  /**
   * 插件的触发语句，触发后将进入插件的触发语句内
   */
  override val trigger: String => Boolean = _ => true

  /**
   * 处理事件的消息，
   * 如果返回true，表示已处理完毕。
   * 如果返回false，表示本插件的处理未完成，接下来用户的消息无需trigger校验直接进入到本插件处理。
   *
   * @param msg
   * @return
   */
  override def handle(msg: MessageEvent): Unit = {
    repeat(msg)
    true
  }
}
