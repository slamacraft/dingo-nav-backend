package com.dingdo.mirai.context

import scala.collection.immutable.LinearSeq
import scala.collection.{AbstractSeq, LinearSeqLike, mutable}

class MsgCacheContext {

}

class GroupMsg {
  var groupId: Long = _
  var userMsgQueue: MsgQueue[UserMsg] = _
  userMsgQueue
  .
}

case class UserMsg(userId: Long, msg: String)

class MsgQueue[A](size: Int)
  extends mutable.Iterable[A] {

  val msgArray = new Array[A](size)
  var len: Int = 0
  var index: Int = 0

  def add(value: A): Unit = {
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