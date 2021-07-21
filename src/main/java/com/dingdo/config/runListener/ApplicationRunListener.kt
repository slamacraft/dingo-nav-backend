package com.dingdo.config.runListener

import com.dingdo.common.util.SpringContextUtil.applicationContext
import com.dingdo.game.cdda.data.component.CddaInitializer
import com.dingdo.robot.mirai.config.BotInfoConfiguration
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent

open class ApplicationRunListener : ApplicationListener<ContextRefreshedEvent> {

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        println("================= 容器初始化完毕 ================")
        applicationContext = event.applicationContext
        BotInfoConfiguration.initBots()
        CddaInitializer.initGame()
    }
}
