package com.dingdo.mirai.core

import com.dingdo.config.properties.BotConfig
import com.dingdo.mirai.model.MsgSaver
import com.dingdo.util.FileUtil
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
    MsgSaver.saveMsg(msg)
    true
  }
}

// todo 需要处理用户长时间处于插件内的情况，插件处理器待优化，这玩意需要做的比较复杂
object BotPluginHandler extends BotMsgHandlerChain {
  val plugins = new mutable.MutableList[BotPlugin]()

  override def next: BotMsgHandlerChain = null

  override def handle(msg: MessageEvent): Boolean = {
    val msgStr = msg.getMessage.contentToString()
    plugins.find(_.trigger(msgStr))
      .forall(_.handle(msg))
  }

  def registerPlugin(plugin: BotPlugin): Unit = plugins += plugin
}

