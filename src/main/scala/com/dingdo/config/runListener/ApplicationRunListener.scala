package com.dingdo.config.runListener

import com.dingdo.config.runListener.ApplicationRunListener.logger
import com.dingdo.util.SpringContextUtil
import lombok.extern.slf4j.Slf4j
import org.apache.log4j.Logger
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent

@Slf4j
class ApplicationRunListener extends ApplicationListener[ContextRefreshedEvent] {

  override def onApplicationEvent(event: ContextRefreshedEvent): Unit = {
    SpringContextUtil.setApplicationContext(event.getApplicationContext)
    logger.info("=========== 容器初始化完成 ===========")
  }
}

object ApplicationRunListener {
  private val logger: Logger = Logger.getLogger(classOf[ApplicationRunListener])
}