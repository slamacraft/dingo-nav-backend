package com.dingdo.config.runListener

import com.dingdo.common.util.SpringContextUtil
import com.dingdo.plugin.game.cdda.data.component.CddaInitializer
import com.dingdo.core.robot.mirai.config.BotInfoConfiguration
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent

open class ApplicationRunListener : ApplicationListener<ContextRefreshedEvent> {

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        println("================= 容器初始化完毕 ================")
        SpringContextUtil.applicationContext = event.applicationContext
//        BotInfoConfiguration.initBots()
//        CddaInitializer.initGame()
    }
}
