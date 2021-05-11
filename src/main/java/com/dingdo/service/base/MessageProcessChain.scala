package com.dingdo.service.base

import com.dingdo.robot.botDto.{ReplyMsg, ReqMsg}

trait MessageProcessChain extends PriorityConfigurable {

  def isMatch(reqMsg: ReqMsg): Boolean

  def invokeProcess(reqMsg: ReqMsg): ReplyMsg

}
