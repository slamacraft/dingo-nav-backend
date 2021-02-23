package com.dingdo.robot.miraiScala

import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.{FriendMessageEvent, GroupMessageEvent}
import net.mamoe.mirai.{Bot, Mirai}

import java.util
import scala.collection.mutable
import scala.collection.JavaConverters._

object MiraiRobotLander {

  private var bots = mutable.HashMap.empty[Long, Bot]

  def robotLogin(initBotInfo: Map[Long, String]) {
    initBotInfo.foreach(info => {
      val bot = Mirai.getInstance().getBotFactory.newBot(info._1, info._2)
      //      bot.login()
      bots(info._1) = bot
    })
  }

  def registeredGroupMsgEvent(eventMethod: GroupMessageEvent => Unit): Unit =
    GlobalEventChannel.INSTANCE.subscribeAlways(classOf[GroupMessageEvent], eventMethod(_))


  def registeredFriendMsgEvent(eventMethod: FriendMessageEvent => Unit): Unit =
    GlobalEventChannel.INSTANCE.subscribeAlways(classOf[FriendMessageEvent], eventMethod(_))

  def getBotInfo(id: Long): Option[Bot] = bots.get(id)

  def getBotList: util.List[Bot] = bots.values.toList.asJava

}
