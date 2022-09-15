package com.dingdo.mirai.core

import net.mamoe.mirai.event.{GlobalEventChannel, Listener}
import net.mamoe.mirai.event.events.{FriendMessageEvent, GroupMessageEvent}

trait MsgHandler {

  val eventChannel: GlobalEventChannel = GlobalEventChannel.INSTANCE

  def handleGroupMsg: GroupMessageEvent => Unit = { _ => }

  def handleFriendMsg: FriendMessageEvent => Unit = { _ => }

  def register: (Listener[GroupMessageEvent], Listener[FriendMessageEvent]) = {
    val groupListener = eventChannel.subscribeAlways(classOf[GroupMessageEvent], (event: GroupMessageEvent) => {
      handleGroupMsg(event)
    })
    val friendListener = eventChannel.subscribeAlways(classOf[FriendMessageEvent], (event: FriendMessageEvent) => {
      handleFriendMsg(event)
    })
    (groupListener, friendListener)
  }
}
