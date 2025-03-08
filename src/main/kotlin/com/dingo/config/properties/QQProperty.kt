package com.dingo.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "qq")
open class QQProperty {
    lateinit var url: String
    var botId: Long = 0
    lateinit var appId: String
    lateinit var token: String
    lateinit var secret: String

    init {
        instance = this
    }

    companion object {
        lateinit var instance: QQProperty
    }
}