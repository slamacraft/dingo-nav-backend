package com.dingdo.plugin.basis

import com.dingdo.core.mirai.BotMsg
import com.dingdo.core.plugin.{BotPlugin, BotPluginImpl}
import org.springframework.stereotype.Component

@Component
class SaverPlugin extends BotPluginImpl[] {
  override val name: String = "消息存储"

  override def parentName(): String = {

  }

  /**
   * 插件会如何处理单条消息
   */
  override def apply(msg: BotMsg): BotMsg = ???
}
