package com.dingdo.config.runListener

import com.dingdo.RobotApplication
import com.dingdo.common.PackageScanner
import com.dingdo.context.SpringContext
import com.dingdo.core.robot.mirai.config.BotInfoConfiguration
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
