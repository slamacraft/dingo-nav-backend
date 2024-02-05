package com.dingo.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "minio")
open class MinioProperty {
    lateinit var url:String
    lateinit var key: String
    lateinit var secret: String
}