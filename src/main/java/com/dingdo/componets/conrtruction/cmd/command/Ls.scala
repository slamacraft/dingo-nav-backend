package com.dingdo.componets.conrtruction.cmd.command

import com.dingdo.componets.conrtruction.base.ConstructionReply
import com.dingdo.componets.conrtruction.cmd.CmdCommand
import com.dingdo.componets.conrtruction.manager.user.User
import com.dingdo.robot.botDto.ReqMsg
import com.dingdo.robot.botDto.factory.BotDtoFactory
import com.dingdo.service.base.DefaultResponder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class Ls extends CmdCommand {

  @Autowired
  var defaultResponder: DefaultResponder = _

  override def command(): String = "ls"

  override def run(reqMsg: ReqMsg, user: User): ConstructionReply = {
    ConstructionReply.suspend(BotDtoFactory.replyMsg("这个功能还没完成"), defaultResponder.defaultReply(reqMsg, _))
  }
}
