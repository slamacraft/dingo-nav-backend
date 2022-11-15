package com.dingdo.config

import com.dingdo.common.util.SpringContextUtil
import org.springframework.beans.factory.annotation.Configurable
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent

@Configurable
class AppConfig extends ApplicationListener[ContextRefreshedEvent] {

  override def onApplicationEvent(event: ContextRefreshedEvent): Unit = {
    SpringContextUtil.setApplicationContext(event.getApplicationContext)
  }
}
