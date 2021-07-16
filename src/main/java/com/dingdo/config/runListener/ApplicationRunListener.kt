package com.dingdo.config.runListener

import com.dingdo.common.util.SpringContextUtil.applicationContext
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent

open class ApplicationRunListener : ApplicationListener<ContextRefreshedEvent> {

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        applicationContext = event.applicationContext
    }
}
