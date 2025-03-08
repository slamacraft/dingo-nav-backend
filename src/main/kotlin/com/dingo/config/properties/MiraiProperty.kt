package com.dingo.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "mirai")
open class MiraiProperty {
    var id: Long = 0
    lateinit var pw: String

    init {
        instance = this
    }

    companion object {
        lateinit var instance: MiraiProperty
    }
}
