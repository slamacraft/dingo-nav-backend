package com.dingdo.plugin.basis

import com.dingdo.core.mirai.BotMsg
import com.dingdo.core.plugin.BotPlugin
import org.springframework.stereotype.Component

@Component
class MsgFilterPlugin extends BotPlugin{
  override val name: String = "消息过滤"

  override def parentName(): String = {

  }

  /**
   * 插件会如何处理单条消息
   */
  override def apply(msg: BotMsg): BotMsg = ???
}
