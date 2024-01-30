package com.dingo.config.runListener

import com.dingo.RobotApplication
import com.dingo.common.PackageScanner
import com.dingo.context.SpringContext
import com.dingo.core.robot.mirai.config.BotInfoConfiguration
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent

open class ApplicationRunListener : ApplicationListener<ContextRefreshedEvent> {

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        println("================= 容器初始化完毕 ================")
        SpringContext.applicationContext = event.applicationContext
        BotInfoConfiguration.initBots()
        PackageScanner.doScan(RobotApplication::class.java)
//        CddaInitializer.initGame()
    }
}
