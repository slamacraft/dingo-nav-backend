package com.dingdo.config

import com.dingdo.config.AppConfig.logger
import com.dingdo.util.SpringContextUtil
import lombok.extern.slf4j.Slf4j
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Configurable
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Bean
import org.springframework.context.event.ContextRefreshedEvent

@Configurable
class AppConfig extends ApplicationListener[ContextRefreshedEvent] {

  override def onApplicationEvent(event: ContextRefreshedEvent): Unit = {
    SpringContextUtil.setApplicationContext(event.getApplicationContext)
    logger.info("=========== 容器初始化完成 ===========")
  }
}

object AppConfig {
  private val logger: Logger = Logger.getLogger(classOf[AppConfig])
}
