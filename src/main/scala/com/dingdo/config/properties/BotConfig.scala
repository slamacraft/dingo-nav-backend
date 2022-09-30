package com.dingdo.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "bot.config")
class BotConfig {
  var cacheQueueSize:Int = _
  var filterFile: String = _

  BotConfig.cfg = this
}

object BotConfig {
  var cfg:BotConfig = _
}