package com.dingdo.componets.conrtruction

import com.dingdo.componets.conrtruction.base.ConstructionReply
import com.dingdo.componets.conrtruction.manager.user.User
import com.dingdo.robot.botDto.ReqMsg

/**
 * 指令接口
 */
trait Construction {

  val NonStatus = "-1"

  def value: String

  def check(reqMsg: ReqMsg, user: User): Boolean

  def run(reqMsg: ReqMsg, user: User): ConstructionReply

  def preOperate(reqMsg: ReqMsg, user: User): Unit = ()=>()

  def postOperate(reqMsg: ReqMsg, user: User): Unit = ()=>()
}
