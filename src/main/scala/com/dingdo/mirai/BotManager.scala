package com.dingdo.mirai

import scala.collection.mutable

object BotManager {

  private val botList: mutable.MutableList[MiraiBot] = mutable.MutableList[MiraiBot]()

  private[mirai] def registerBot(bot: MiraiBot): Unit = {
    botList += bot
  }

  def getBot(id: Long): MiraiBot = botList.find(_.id == id).orNull

}
