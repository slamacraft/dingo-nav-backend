package com.dingdo.mirai.core

import net.mamoe.mirai.event.events.MessageEvent

trait BotPlugin {

  BotPluginHandler.registerPlugin(this)

  val trigger: String

  def handle(msg: MessageEvent): Boolean


}
