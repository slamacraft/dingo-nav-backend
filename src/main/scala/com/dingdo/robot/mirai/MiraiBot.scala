package com.dingdo.robot.mirai

import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.utils.BotConfiguration
import net.mamoe.mirai.{Bot, BotFactory}


class MiraiBot(val id: Long, pw: String) {

  // mirai定义的bot
  val bot: Bot = MiraiBot.createAndLoginBot(id, pw)


}

object MiraiBot {

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
    miraiBot
  }

  def main(args: Array[String]): Unit = {
    val bot = MiraiBot(1, "1")
    val eventChannel = GlobalEventChannel.INSTANCE
    val value = eventChannel.subscribeAlways(classOf[FriendMessageEvent], (event: FriendMessageEvent) => {
    })
  }
}