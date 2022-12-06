package com.dingdo.core

import com.dingdo.core.mirai.{BotMsg, OneMsg}

trait BotPlugin {

  val name: String

  /**
   * 插件会如何处理单条消息
   */
  def apply(msg: OneMsg): BotMsg

}

object BotPlugin extends BotPlugin {
  override val name: String = "ROOT"

  /**
   * 插件会如何处理单条消息
   */
  override def apply(msg: OneMsg): BotMsg = msg
}
