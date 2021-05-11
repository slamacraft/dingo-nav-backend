package com.dingdo.service.base

import cn.hutool.core.util.StrUtil
import com.dingdo.robot.botDto.ReqMsg
import com.dingdo.robot.botDto.factory.BotDtoFactory
import com.dingdo.robot.botService.GroupMsgService
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import scala.collection.mutable


@Component
class RepeatComponent {

  @Autowired
  private var groupMsgService: GroupMsgService = _

  val groupMsgMap: mutable.HashMap[String, UserMsg] = mutable.HashMap()


  def repeat(reqMsg: ReqMsg): Unit = {
    val groupMsg = groupMsgMap.getOrElseUpdate(reqMsg.getSource.getGroupId, new UserMsg())

    // 复读触发条件
    val repeatFlag = groupMsg.msg.endsWith(reqMsg.getMsg)

    groupMsg.repeat =
      if (repeatFlag && !groupMsg.repeat && StringUtils.isNotBlank(groupMsg.msg)) sendRepeat(reqMsg)
      else if (!repeatFlag) false
      else groupMsg.repeat

    groupMsg.userId = reqMsg.getSource.getUserId
    groupMsg.msg = reqMsg.getMsg
  }


  /**
   * 发送复读语句
   *
   * @param reqMsg 请求消息
   * @return true
   */
  def sendRepeat(reqMsg: ReqMsg): Boolean = {
    val reply = BotDtoFactory.replyMsg(reqMsg.getMsg)
    groupMsgService.sendMsg(reqMsg.getSource.getSelfId, reqMsg.getSource.getGroupId, reply)
    true
  }

}

class UserMsg {
  var userId: String = "-1"
  var msg: String = ""
  var repeat: Boolean = false
}