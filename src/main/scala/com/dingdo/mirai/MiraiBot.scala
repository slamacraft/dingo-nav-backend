package com.dingdo.mirai

import com.dingdo.mirai.core.MsgHandlerChain
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.utils.BotConfiguration
import net.mamoe.mirai.{Bot, BotFactory}


class MiraiBot(val id: Long, pw: String) {

  // mirai定义的bot
  val bot: Bot = MiraiBot.createAndLoginBot(id, pw)

  MiraiBot.bot = this
}

object MiraiBot {

  var bot:MiraiBot = _
  val eventChannel: GlobalEventChannel = GlobalEventChannel.INSTANCE

  def apply(id: Long, pw: String): MiraiBot = new MiraiBot(id, pw)

  private def createAndLoginBot(id: Long, pw: String): Bot = {
    val botFactory: BotFactory = BotFactory.INSTANCE

    val miraiBot = botFactory.newBot(id, pw, (config: BotConfiguration) => {
      config.fileBasedDeviceInfo()
      // 切换心跳策略
      config.setHeartbeatStrategy(BotConfiguration.HeartbeatStrategy.REGISTER)
      // 开启群成员列表缓存
      val cache = config.getContactListCache
      cache.setGroupMemberListCacheEnabled(true)
    })

    miraiBot.login()

    // 注册事件
    eventChannel.subscribeAlways(classOf[MessageEvent], (event: MessageEvent) => {
      MsgHandlerChain.handle(event)
    })

    miraiBot
  }

  def main(args: Array[String]): Unit = {
    val bot = MiraiBot(1, "1")
  }
}