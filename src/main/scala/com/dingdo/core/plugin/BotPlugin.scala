package com.dingdo.core.plugin

import com.dingdo.core.mirai.BotMsg

trait BotPlugin {

  val name: String

  def parentName(): String

  /**
   * 插件会如何处理单条消息
   */
  def apply(msg: BotMsg): BotMsg

}

object BotPlugin extends BotPlugin {
  override val name: String = "ROOT"

  /**
   * 插件会如何处理单条消息
   */
  override def apply(msg: BotMsg): BotMsg = msg

  override def parentName(): String = ""
}
