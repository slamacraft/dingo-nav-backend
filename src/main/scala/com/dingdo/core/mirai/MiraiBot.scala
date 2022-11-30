package com.dingdo.core.mirai

import com.dingdo.core.mirai.context.MsgCacheContext
import com.dingdo.core.mirai.core.MsgHandlerChain
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.utils.BotConfiguration
import net.mamoe.mirai.utils.BotConfiguration.MiraiProtocol
import net.mamoe.mirai.{Bot, BotFactory}


class MiraiBot(val id: Long, val pw: String) {

  // mirai定义的bot
  private val bot: Bot = MiraiBot.botFactory.newBot(id, pw, (config: BotConfiguration) => {
    config.fileBasedDeviceInfo()
    config.setHeartbeatStrategy(BotConfiguration.HeartbeatStrategy.REGISTER) // 切换心跳策略
    config.getContactListCache.setGroupMemberListCacheEnabled(true) // 开启群成员列表缓存
    config.setProtocol(MiraiProtocol.ANDROID_PAD)
  })

  // 消息缓存
  val msgCache = new MsgCacheContext(id)

  def login: MiraiBot = {
//    bot.login()
    BotManager.registerBot(this) // 登录后立即注册到管理器
    this
  }
}

object MiraiBot {
  val botFactory: BotFactory = BotFactory.INSTANCE
  val eventChannel: GlobalEventChannel = GlobalEventChannel.INSTANCE
  // 注册全局事件处理器
  MiraiBot.eventChannel.subscribeAlways(classOf[MessageEvent], (event: MessageEvent) => {
    MsgHandlerChain.handle(event)
  })

  def apply(id: Long, pw: String): MiraiBot = new MiraiBot(id, pw)
}