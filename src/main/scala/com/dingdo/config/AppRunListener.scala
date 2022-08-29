package com.dingdo.config

import com.dingdo.config.AppRunListener.logger
import com.dingdo.util.SpringContextUtil
import lombok.extern.slf4j.Slf4j
import org.apache.log4j.Logger
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent

@Slf4j
class AppRunListener extends ApplicationListener[ContextRefreshedEvent] {

  override def onApplicationEvent(event: ContextRefreshedEvent): Unit = {
    SpringContextUtil.setApplicationContext(event.getApplicationContext)
    logger.info("=========== 容器初始化完成 ===========")
  }
}

object AppRunListener {
  private val logger: Logger = Logger.getLogger(classOf[AppRunListener])
}