package com.dingdo.mirai.core

import com.dingdo.config.properties.BotConfig
import com.dingdo.mirai.MsgSaver
import com.dingdo.common.util.FileUtil
import net.mamoe.mirai.event.events.MessageEvent

import java.util.stream.Collectors
import scala.collection.JavaConverters._
import scala.collection.mutable

sealed trait BotMsgHandlerChain {
  def next: BotMsgHandlerChain

  def handle(msg: MessageEvent): Boolean
}

object MsgHandlerChain extends BotMsgHandlerChain {
  override def next: BotMsgHandlerChain = MsgFilterHandler

  override def handle(msg: MessageEvent): Boolean = {
    var handler = next
    var continue = true
    while (handler != null && continue) {
      continue = handler.handle(msg)
      handler = handler.next
    }
    true
  }
}

// 需要重做，可配置化
object MsgFilterHandler extends BotMsgHandlerChain {

  private var filterList: List[String] = _

  override def next: BotMsgHandlerChain = MsgSaverHandler

  override def handle(msg: MessageEvent): Boolean = {
    val msgContent = msg.getMessage.stream()
      .map[String](_.contentToString())
      .filter(it => getFilterList.forall(filter => it.matches(filter)))
      .collect(Collectors.joining())

    msgContent.nonEmpty
  }

  private def getFilterList: List[String] = {
    if (filterList != null) return filterList
    filterList = FileUtil.loadFileFromResource(BotConfig.cfg.filterFile) {
      _.lines().collect(Collectors.toList[String]())
    }
      .asScala.toList
    filterList
  }
}

object MsgSaverHandler extends BotMsgHandlerChain {
  override def next: BotMsgHandlerChain = BotPluginHandler

  override def handle(msg: MessageEvent): Boolean = {
    MsgSaver.instance += msg
    true
  }
}

object BotPluginHandler extends BotMsgHandlerChain {
  val parallelPlugins = new mutable.MutableList[ParallelBotPlugin]()
  val blockPlugins = new mutable.MutableList[BlockBotPlugin]()
  val pluginHolder = new mutable.HashMap[Long, BlockBotPlugin]()

  override def next: BotMsgHandlerChain = null

  override def handle(msg: MessageEvent): Boolean = {
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

  def registerPlugin(plugin: BotPlugin): Unit = plugin match {
    case p: ParallelBotPlugin => parallelPlugins += p
    case p: BlockBotPlugin => blockPlugins += p
  }
}

