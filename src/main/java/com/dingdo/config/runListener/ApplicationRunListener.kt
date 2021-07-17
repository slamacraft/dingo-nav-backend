package com.dingdo.config.runListener

import com.dingdo.common.util.SpringContextUtil.applicationContext
import com.dingdo.robot.mirai.MiraiRobotInitializer
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent

open class ApplicationRunListener : ApplicationListener<ContextRefreshedEvent> {

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        println("\\033[0m================= 容器初始化完毕 ================")
        applicationContext = event.applicationContext
    }
}
