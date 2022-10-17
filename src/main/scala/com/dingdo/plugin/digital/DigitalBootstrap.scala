package com.dingdo.plugin.digital

import com.dingdo.mirai.core.{BlockBotPlugin, BotPlugin}
import net.mamoe.mirai.event.events.MessageEvent

object DigitalBootstrap extends BlockBotPlugin {

  override val trigger: String => Boolean = _.startsWith("王权")

  override def handle(msg: MessageEvent): Boolean = true


}
