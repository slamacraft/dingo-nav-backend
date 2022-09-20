package com.dingdo.mirai.core

import com.dingdo.mirai.model.MsgSaver
import net.mamoe.mirai.event.events.MessageEvent

import scala.collection.mutable

sealed trait BotMsgHandlerChain {
  def next:BotMsgHandlerChain
  def handle(msg: MessageEvent):Boolean
}

object MsgHandlerChain extends BotMsgHandlerChain{
  override def next: BotMsgHandlerChain = MsgSaverHandler
  override def handle(msg: MessageEvent): Boolean = {
    var handler = next
    var continue = true
    while (handler != null && continue){
      continue = handler.handle(msg)
      handler = handler.next
    }
    true
  }
}

object MsgSaverHandler extends BotMsgHandlerChain{
  override def next: BotMsgHandlerChain = BotPluginHandler
  override def handle(msg: MessageEvent): Boolean = {
    MsgSaver.saveMsg(msg)
    true
  }
}

object BotPluginHandler extends BotMsgHandlerChain{
  val plugins = new mutable.HashMap[String, BotPlugin]()

  override def next: BotMsgHandlerChain = null
  override def handle(msg: MessageEvent): Boolean = {
    val trigger = msg.getMessage.contentToString()
    plugins.get(trigger)
      .forall(it => it.handle(msg))
  }

  def registerPlugin(plugin: BotPlugin): Unit = plugins(plugin.trigger) = plugin
}

