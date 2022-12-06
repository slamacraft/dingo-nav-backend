package com.dingdo.plugin.basis

import com.dingdo.core.BotPlugin
import com.dingdo.core.mirai.{BotMsg, OneMsg}
import com.dingdo.plugin.basis.model.entity.BotMsgEntity
import com.dingdo.plugin.basis.model.mapper.BotMsgMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MsgSaverPlugin extends BotPlugin {
  override val name: String = "消息存储"
  @Autowired
  private var botMsgMapper: BotMsgMapper = _

  /**
   * 插件会如何处理单条消息
   */
  override def apply(msg: OneMsg): BotMsg = {
    botMsgMapper.insert(BotMsgEntity.build(msg.sourceMsg))
    msg
  }
}
