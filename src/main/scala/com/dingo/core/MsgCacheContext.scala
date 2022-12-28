package com.dingo.core

import com.dingo.config.properties.BotConfig
import com.dingo.core.OneMsg
import net.mamoe.mirai.event.events.{FriendMessageEvent, GroupMessageEvent, MessageEvent}

import scala.collection.mutable
import scala.language.implicitConversions
import scala.reflect.ClassTag

class MsgCacheContext(val botId: Long) {
  private val groupMsg = new mutable.HashMap[Long, GroupMsg]()
  private val userMsg = new mutable.HashMap[Long, MsgQueue[MsgContent]]()

  def cache(event: MessageEvent): Unit = event match {
    case e: GroupMessageEvent =>
      group(e.getGroup.getId).msg.push(event)
  }

  private implicit def groupMsgEvent(event: MessageEvent): MsgContent = {
    MsgContent(event.getSender.getId, event.getMessage.contentToString())
  }

  def group(groupId: Long): GroupMsg =
    groupMsg.getOrElseUpdate(groupId, new GroupMsg(groupId))

  def user(userId: Long): MsgQueue[MsgContent] =
    userMsg.getOrElseUpdate(userId, new MsgQueue[MsgContent](BotConfig.cfg.cacheQueueSize))
}

class GroupMsg(val groupId: Long) {
  var msg: MsgQueue[MsgContent] = new MsgQueue(BotConfig.cfg.cacheQueueSize)
}

//case class UserMsg(userId: Long, msg: String)

class MsgQueue[A: ClassTag](size: Int) extends mutable.Iterable[A] {

  val msgArray = new Array[A](size)
  var len: Int = 0
  var index: Int = 0

  def push(value: A): Unit = {
    len = if (len + 1 < size) len + 1 else size
    index = idx(index + 1)
    msgArray(index) = value
  }

  private def range: Range = index until index + len

  private def idx(i: Int): Int = i % size

  override def iterator: Iterator[A] = {
    val list = for (i <- range) yield msgArray(idx(i))
    list.iterator
  }
}