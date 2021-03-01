package com.dingdo.service.base

import com.dingdo.robot.botDto.{ReplyMsg, ReqMsg}
import com.dingdo.robot.botService.{GroupMsgService, PrivateMsgService}
import com.dingdo.robot.enums.MsgTypeEnum
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Component
class DefaultResponder {


  @Autowired
  private var groupMsgService: GroupMsgService = _

  @Autowired
  private var privateMsgService: PrivateMsgService = _


  def defaultReply(req: ReqMsg, reply: ReplyMsg): Unit = {
    req.getType match {
      case MsgTypeEnum.PRIVATE => defaultPrivateReply(req, reply)
      case MsgTypeEnum.GROUP => defaultGroupReply(req, reply)
      case _ => Unit
    }
  }


  def defaultGroupReply(req: ReqMsg, reply: ReplyMsg): Unit = {
    groupMsgService.sendMsg(req.getSelfId, req.getGroupId, reply)
  }

  def defaultPrivateReply(req: ReqMsg, reply: ReplyMsg): Unit = {
    privateMsgService.sendMsg(req.getSelfId, req.getUserId, reply)
  }
}
