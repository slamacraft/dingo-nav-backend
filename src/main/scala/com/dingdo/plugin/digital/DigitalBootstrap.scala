package com.dingdo.plugin.digital

import com.dingdo.mirai.core.BotPlugin
import net.mamoe.mirai.event.events.MessageEvent

object DigitalBootstrap extends BotPlugin{

  override val trigger: String = "123123123123"

  override def handle(msg: MessageEvent): Boolean = true


}
