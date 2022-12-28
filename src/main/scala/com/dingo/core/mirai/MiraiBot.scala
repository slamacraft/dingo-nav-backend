package com.dingo.core.mirai

import com.dingo.core.{BotPluginManager, MsgCacheContext}
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.MessageRecallEvent.GroupRecall
import net.mamoe.mirai.event.events.{GroupMessageEvent, MessageEvent}
import net.mamoe.mirai.utils.BotConfiguration
import net.mamoe.mirai.utils.BotConfiguration.MiraiProtocol
import net.mamoe.mirai.{Bot, BotFactory}


class MiraiBot(val id: Long, val pw: String) {

  // mirai定义的bot
  val bot: Bot = MiraiBot.botFactory.newBot(id, pw, (config: BotConfiguration) => {
    config.fileBasedDeviceInfo()
    config.setHeartbeatStrategy(BotConfiguration.HeartbeatStrategy.REGISTER) // 切换心跳策略
    config.getContactListCache.setGroupMemberListCacheEnabled(true) // 开启群成员列表缓存
    config.setProtocol(MiraiProtocol.ANDROID_PAD)
  })


  // 消息缓存
  val msgCache = new MsgCacheContext(id)


  def login: MiraiBot = {
    bot.login()
    BotManager.registerBot(this) // 登录后立即注册到管理器
    this
  }


}

object MiraiBot {
  private val botFactory: BotFactory = BotFactory.INSTANCE
  private val eventChannel: GlobalEventChannel = GlobalEventChannel.INSTANCE

  // 注册全局事件处理器
  MiraiBot.eventChannel.subscribeAlways(classOf[GroupMessageEvent], (event: GroupMessageEvent) => {
    BotPluginManager.INSTANCE.handle(event)
  })

  MiraiBot.eventChannel.subscribeAlways(classOf[GroupRecall], (event: GroupRecall) => {
    //    BotPluginHandler.INSTANCE.handle(event)
    println("有人删了文件")
    event.getMessageIds
  })

  def apply(id: Long, pw: String): MiraiBot = new MiraiBot(id, pw)
}