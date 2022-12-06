package com.dingdo.core.plugin

import com.dingdo.core.BotPluginHandler
import com.dingdo.core.mirai.core.BotPluginHandler
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

  def toConfig: BotPluginHandler#PluginConfig = {
    new BotPluginHandler.PluginConfig
  }
}
