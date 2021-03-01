package com.dingdo.componets.conrtruction.cmd

import com.dingdo.componets.conrtruction.LockStatusConstruction
import com.dingdo.componets.conrtruction.base.ConstructionReply
import com.dingdo.componets.conrtruction.cmd.factory.CommandComponent
import com.dingdo.componets.conrtruction.manager.user.User
import com.dingdo.robot.botDto.ReqMsg
import com.dingdo.robot.botDto.factory.BotDtoFactory
import com.dingdo.service.base.DefaultResponder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CmdConstructor extends LockStatusConstruction {

  @Autowired
  var commandComponent: CommandComponent = _

  @Autowired
  var defaultResponder: DefaultResponder = _

  override def value: String = ".cmd"

  override def check(reqMsg: ReqMsg, user: User): Boolean = {
    true
  }

  override def run(reqMsg: ReqMsg, user: User): ConstructionReply = {
    val message = if (!".cmd".equals(reqMsg.getMsg)) reqMsg.getMsg else ""
    defaultResponder.defaultReply(reqMsg, BotDtoFactory.replyMsg(s"[${reqMsg.getNickname}@${reqMsg.getUserId}]>$message"))
    reqMsg.getMsg match {
      case ".cmd" => ConstructionReply.non()
      case "exit" => ConstructionReply.success(BotDtoFactory.replyMsg("bye!"), defaultResponder.defaultReply(reqMsg, _))
      case _ => commandComponent.executeCommand(reqMsg, user)
    }
  }


}
