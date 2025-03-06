package com.dingo.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "dify")
open class DifyProperty {
    init {
        instance = this
    }

    lateinit var url: String
    lateinit var port: String
    lateinit var appKey: String

    companion object{
        lateinit var instance: DifyProperty
    }
}