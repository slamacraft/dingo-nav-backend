package com.dingdo.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "bot.info")
object BotProperties {
  var id: Long = _
  var pw: String = _
  var name: String = _
}