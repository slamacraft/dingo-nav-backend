package com.dingo.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "security")
open class SecurityProperty {
    // 白名单
    lateinit var whiteIp: List<String>

}