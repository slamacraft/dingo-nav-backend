package com.dingdo.componets.conrtruction

import com.dingdo.componets.conrtruction.manager.user.User
import com.dingdo.robot.botDto.ReqMsg

trait LockStatusConstruction extends StatusConstruction {

  override def preOperate(reqMsg: ReqMsg, user: User): Unit = {
    super.preOperate(reqMsg, user)
    user.constructionStatus.lockFlag = true
  }

  override def postOperate(reqMsg: ReqMsg, user: User): Unit = {
    super.postOperate(reqMsg, user)
    user.constructionStatus.lockFlag = false
  }

}
