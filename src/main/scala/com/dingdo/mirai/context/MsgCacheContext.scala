package com.dingdo.mirai.context

import com.dingdo.config.properties.BotConfig
import net.mamoe.mirai.event.events.{FriendMessageEvent, GroupMessageEvent, MessageEvent}

import scala.collection.mutable
import scala.language.implicitConversions
import scala.reflect.ClassTag

object MsgCacheContext {
  private val groupMsg = new mutable.HashMap[Long, GroupMsg]()
  private val userMsg = new mutable.HashMap[Long, MsgQueue[UserMsg]]()

  def cache(event: MessageEvent): Unit = event match {
    case e: GroupMessageEvent =>
      group(e.getGroup.getId).msg.push(event)
    case e: FriendMessageEvent =>
      user(e.getSender.getId).push(event)
  }


  private implicit def groupMsgEvent(event: MessageEvent): UserMsg = {
    UserMsg(event.getSender.getId, event.getMessage.contentToString())
  }

  def group(groupId: Long): GroupMsg =
    groupMsg.getOrElseUpdate(groupId, new GroupMsg(groupId))

  def user(userId: Long): MsgQueue[UserMsg] =
    userMsg.getOrElseUpdate(userId, new MsgQueue[UserMsg](BotConfig.cfg.cacheQueueSize))
}

class GroupMsg(val groupId: Long) {
  var msg: MsgQueue[UserMsg] = new MsgQueue(BotConfig.cfg.cacheQueueSize)
}

case class UserMsg(userId: Long, msg: String)

class MsgQueue[A](size: Int)(implicit val classTag:ClassTag[A]) extends mutable.Iterable[A] {

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