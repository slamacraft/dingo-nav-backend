package com.dingdo.mirai.core

import net.mamoe.mirai.event.events.MessageEvent

trait BotPlugin {

  BotPluginHandler.registerPlugin(this)

  /**
   * 插件的触发语句，触发后将进入插件的触发语句内
   */
  val trigger: String

  def handle(msg: MessageEvent): Boolean

}
