package com.dingdo.mirai.core

import net.mamoe.mirai.event.events.MessageEvent

trait BotPlugin {

  BotPluginHandler.registerPlugin(this)

  /**
   * 插件的触发语句，触发后将进入插件的触发语句内
   */
  val trigger: String => Boolean

  /**
   * 处理事件的消息，
   * 如果返回true，表示已处理完毕。
   * 如果返回false，表示本插件的处理未完成，接下来用户的消息无需trigger校验直接进入到本插件处理。
   * @param msg
   * @return
   */
  def handle(msg: MessageEvent): Boolean

}
