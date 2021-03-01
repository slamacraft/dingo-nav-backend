package com.dingdo.componets.conrtruction
import com.dingdo.componets.conrtruction.manager.user.User
import com.dingdo.robot.botDto.ReqMsg

trait StatusConstruction extends Construction{

  override def preOperate(reqMsg: ReqMsg, user: User): Unit = {
    user.constructionStatus.status = value
  }

  override def postOperate(reqMsg: ReqMsg, user: User): Unit = {
    user.constructionStatus.status = NonStatus
  }
}
