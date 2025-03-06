package com.dingo.config.properties

import com.dingo.config.properties.DifyProperty.Companion
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "bot")
open class BotInfoProperty {
    var id: Long = 0
    lateinit var pw: String

    init {
        BotInfoProperty.instance = this
    }

    companion object {
        lateinit var instance: BotInfoProperty
    }
}
