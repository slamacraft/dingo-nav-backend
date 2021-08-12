package com.dingdo.robot.mirai.config

import com.dingdo.robot.mirai.MiraiRobotInitializer
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "bots.core")
@EnableConfigurationProperties(BotInfoConfiguration::class)
object BotInfoConfiguration {

    lateinit var loginInfo: List<String>

    fun initBots() {
        val botInfos =
            loginInfo.associateBy(keySelector = { it.split(":")[0].toLong() }, valueTransform = { it.split(":")[1] })
        MiraiRobotInitializer.run(botInfos)
    }

}
