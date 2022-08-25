package com.dingdo.plugin

import net.mamoe.mirai.event.events.MessageEvent

trait BotPlugin {
  def name(): String

  def version(): String

  def order(): Int

  def handle(msg: MessageEvent): Boolean
}
