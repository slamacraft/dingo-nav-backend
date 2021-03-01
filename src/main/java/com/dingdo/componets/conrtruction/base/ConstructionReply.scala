package com.dingdo.componets.conrtruction.base

import com.dingdo.componets.conrtruction.base.ConstructionReply.success
import com.dingdo.robot.botDto.ReplyMsg

trait ConstructionReply {
  def reply(): Unit

  def getReplyMsg: ReplyMsg

  def isSuccess: Boolean

  def isFailure: Boolean
}

object ConstructionReply {
  private val none: NonReply = new NonReply

  def success(replyMsg: ReplyMsg, replyFun: ReplyMsg => Unit): ConstructionReply = new SuccessReply(replyMsg, replyFun)

  def suspend(replyMsg: ReplyMsg, replyFun: ReplyMsg => Unit): ConstructionReply = new SuspendReply(replyMsg, replyFun)

  def non(): ConstructionReply = none
}

class SuccessReply(replyMsg: ReplyMsg, replyFun: ReplyMsg => Unit) extends ConstructionReply {
  override def reply(): Unit = replyFun(replyMsg)

  override def isSuccess: Boolean = true

  override def getReplyMsg: ReplyMsg = replyMsg

  override def isFailure: Boolean = false
}

class SuspendReply(replyMsg: ReplyMsg, replyFun: ReplyMsg => Unit) extends ConstructionReply {
  override def reply(): Unit = replyFun(replyMsg)

  def isSuccess: Boolean = false

  override def getReplyMsg: ReplyMsg = replyMsg

  override def isFailure: Boolean = false
}

class NonReply() extends ConstructionReply {
  override def reply(): Unit = null

  def isSuccess: Boolean = false

  override def getReplyMsg: ReplyMsg = null

  override def isFailure: Boolean = true
}
