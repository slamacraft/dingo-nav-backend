package com.dingdo.mirai.core

import net.mamoe.mirai.event.{GlobalEventChannel, Listener}
import net.mamoe.mirai.event.events.{FriendMessageEvent, GroupMessageEvent}

trait MsgHandler {

  val eventChannel: GlobalEventChannel = GlobalEventChannel.INSTANCE

  val groupListener: Listener[GroupMessageEvent] = eventChannel.subscribeAlways(classOf[GroupMessageEvent], (event: GroupMessageEvent) => {
    handleGroupMsg(event)
  })
  val friendListener: Listener[FriendMessageEvent] = eventChannel.subscribeAlways(classOf[FriendMessageEvent], (event: FriendMessageEvent) => {
    handleFriendMsg(event)
  })

  def handleGroupMsg: GroupMessageEvent => Unit = { _ => }

  def handleFriendMsg: FriendMessageEvent => Unit = { _ => }
}
