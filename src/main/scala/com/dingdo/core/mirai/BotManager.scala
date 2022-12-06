package com.dingdo.core.mirai

import net.mamoe.mirai.Bot

import scala.collection.mutable

object BotManager {

  private val botList: mutable.MutableList[MiraiBot] = mutable.MutableList[MiraiBot]()

  private[mirai] def registerBot(bot: MiraiBot): Unit = {
    botList += bot
  }

  def getBot(id: Long): Option[MiraiBot] = botList.find(_.id == id)

  implicit def getBot(bot: Bot): MiraiBot = getBot(bot.getId).get
}
