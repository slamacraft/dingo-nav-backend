package com.dingdo.componets.conrtruction.cmd

import com.dingdo.componets.conrtruction.base.ConstructionReply
import com.dingdo.componets.conrtruction.manager.user.User
import com.dingdo.robot.botDto.ReqMsg

trait CmdCommand {

  def command():String

  def run(reqMsg: ReqMsg, user: User):ConstructionReply

  def check(reqMsg: ReqMsg, user: User):Boolean = true

}
