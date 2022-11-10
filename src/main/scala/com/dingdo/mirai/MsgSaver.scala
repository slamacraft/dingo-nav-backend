package com.dingdo.mirai

import com.dingdo.mirai.core.MsgHandler
import com.dingdo.model.entity.BotMsgEntity
import com.dingdo.model.mapper.BotMsgMapper
import net.mamoe.mirai.event.events.MessageEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MsgSaver extends MsgHandler {
  @Autowired
  var msgMapper: BotMsgMapper = _
  MsgSaver.instance = this

  def +=(event: MessageEvent): Unit = {
    msgMapper.save(BotMsgEntity.build(event))
  }

  def +=(botMsg:BotMsgEntity): Unit ={
    msgMapper.save(botMsg)
  }
}

object MsgSaver {
  var instance: MsgSaver = _
}