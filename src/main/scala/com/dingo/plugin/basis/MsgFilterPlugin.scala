package com.dingo.plugin.basis


import com.dingo.plugin.basis.model.mapper.MsgFilterConfigMapper
import com.dingo.core.BotPlugin
import com.dingo.core.{BotMsg, NoneMsg, OneMsg}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MsgFilterPlugin extends BotPlugin {
  override val name: String = "消息过滤"
  @Autowired
  private var msgFilterConfigMapper: MsgFilterConfigMapper = _

  /**
   * 插件会如何处理单条消息
   */
  override def apply(msg: OneMsg): BotMsg = {
    val entity = msgFilterConfigMapper.findFirstByBotId(msg.botId)
    if (entity == null || msg.content.matches(entity.partten)) {
      msg
    } else {
      NoneMsg
    }
  }
}
