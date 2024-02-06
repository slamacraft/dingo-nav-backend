package com.dingo.config.listener

import com.dingo.RobotApplication
import com.dingo.common.PackageScanner
import com.dingo.context.SpringContext
import com.dingo.config.properties.BotInfoProperty
import com.dingo.core.mirai.BotInitializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component

@Component
open class ApplicationRunListener : ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    lateinit var botInfoProperty: BotInfoProperty

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        println("================= 容器初始化完毕 ================")
        SpringContext.applicationContext = event.applicationContext
//        BotInitializer.start(botInfoProperty.id.toLong(), botInfoProperty.pw)
//        PackageScanner.doScan(RobotApplication::class.java)
    }
}
