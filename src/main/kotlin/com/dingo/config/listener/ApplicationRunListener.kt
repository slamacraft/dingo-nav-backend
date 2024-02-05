package com.dingo.config.listener

import com.dingo.RobotApplication
import com.dingo.common.PackageScanner
import com.dingo.context.SpringContext
import com.dingo.config.properties.BotInfoProperty
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent

open class ApplicationRunListener : ApplicationListener<ContextRefreshedEvent> {

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        println("================= 容器初始化完毕 ================")
        SpringContext.applicationContext = event.applicationContext
        BotInfoProperty.initBots()
        PackageScanner.doScan(RobotApplication::class.java)
    }
}
