package com.dingdo.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "bot.info")
class BotProperties {
  var id: Long = _
  var pw: String = _
  var name: String = _

  BotProperties.cfg = this
}

object BotProperties {
  var cfg: BotProperties = _
}