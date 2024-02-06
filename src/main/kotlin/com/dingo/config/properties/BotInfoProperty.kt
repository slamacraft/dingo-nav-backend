package com.dingo.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "bot")
open class BotInfoProperty {
    lateinit var id: String
    lateinit var pw: String
}
