package com.dingdo.robot.mirai

import kotlinx.coroutines.CoroutineScope
import net.mamoe.mirai.BotFactory.BotConfigurationLambda
import net.mamoe.mirai.event.{GlobalEventChannel, Listener}
import net.mamoe.mirai.event.events.{FriendMessageEvent, GroupMessageEvent, MessageEvent}
import net.mamoe.mirai.utils.BotConfiguration
import net.mamoe.mirai.{Bot, BotFactory, Mirai}

object MiraiBot {

  def apply(id: Long, pw: String): MiraiBot = new MiraiBot(id, pw)

  def main(args: Array[String]): Unit = {
    val bot = MiraiBot(1, "1")
    val value = bot.registEvent((event: GroupMessageEvent) => {

    })
  }
}

class MiraiBot(val id: Long, pw: String) {

  // mirai定义的bot
  val bot: Bot = createAndLoginBot(id, pw)


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
    miraiBot
  }


  def registEvent[A <: MessageEvent]: (A => Unit) => Listener[A] = {
//    case eventHandle: (GroupMessageEvent => Unit) => {
//      val eventChannel = GlobalEventChannel.INSTANCE
//      eventChannel.subscribeAlways(Class[GroupMessageEvent], (event: A) => {
//        eventHandle(event)
//      })
//    }
    case eventHandle: (FriendMessageEvent => Unit) => {
      val eventChannel = GlobalEventChannel.INSTANCE
      eventChannel.subscribeAlways(Class[FriendMessageEvent], (event: A) => {
        eventHandle(event)
      })
    }
  }


}
