package com.dingdo.core

import com.dingdo.core.mirai.BotMsg

trait BotPlugin {

  val name: String

  /**
   * 插件会如何处理单条消息
   */
  def apply(msg: BotMsg): BotMsg

  def parent(): BotPlugin
}
