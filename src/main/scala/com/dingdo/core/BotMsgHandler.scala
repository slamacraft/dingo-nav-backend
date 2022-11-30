package com.dingdo.core

import com.dingdo.core.mirai.core.BotPluginHandler.{blockPlugins, parallelPlugins, pluginHolder}
import net.mamoe.mirai.event.events.MessageEvent
import org.springframework.context.{ApplicationContext, ApplicationContextAware}
import org.springframework.stereotype.Component


@Component
class BotMsgHandler extends ApplicationContextAware {


  override def setApplicationContext(applicationContext: ApplicationContext): Unit = {
    applicationContext.getBeansOfType(classOf[BotPlugin]).values()
  }

  def handle(msg: MessageEvent): Boolean = {
    val sendId = msg.getSender.getId
    val msgStr = msg.getMessage.contentToString()

    parallelPlugins.filter(_.trigger(msgStr))
      .foreach(_.handle(msg))

    val holdBlockPlugin = pluginHolder.remove(sendId)

    // forall 如果是empty则true，否则为函数的返回值
    holdBlockPlugin.orElse {
      blockPlugins.find(_.trigger(msgStr))
    }.forall { it =>
      val hold = it.handle(msg)
      if (!hold) {
        pluginHolder(sendId) = it
      }
      hold
    }
  }


}
