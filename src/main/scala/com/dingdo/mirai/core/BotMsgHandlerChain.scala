package com.dingdo.mirai.core

import com.dingdo.mirai.model.MsgSaver
import net.mamoe.mirai.event.events.MessageEvent

import scala.collection.mutable

sealed trait BotMsgHandlerChain {
  def next:BotMsgHandlerChain
  def handle(msg: MessageEvent):Unit
}

object MsgHandlerChain extends BotMsgHandlerChain{
  override def next: BotMsgHandlerChain = MsgSaverHandler
  override def handle(msg: MessageEvent): Unit = {
    var handler = next
    while (handler != null){
      handler.handle(msg)
      handler = handler.next
    }
  }
}

object MsgSaverHandler extends BotMsgHandlerChain{
  override def next: BotMsgHandlerChain = BotPluginHandler
  override def handle(msg: MessageEvent): Unit = MsgSaver.saveMsg(msg)
}

object BotPluginHandler extends BotMsgHandlerChain{
  val plugins = new mutable.HashMap[String, BotPlugin]()

  override def next: BotMsgHandlerChain = null
  override def handle(msg: MessageEvent): Unit = {
    val trigger = msg.getMessage.contentToString()
    plugins.apply(trigger)
  }

  def registerPlugin(plugin: BotPlugin): Unit = plugins(plugin.trigger) = plugin
}

