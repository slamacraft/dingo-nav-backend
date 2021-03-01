package com.dingdo.componets.conrtruction.manager.user

import com.dingdo.componets.conrtruction.base.BaseConstructionStatus
import com.dingdo.robot.botDto.ReqMsg

class User(id:String) {

  var constructionStatus: BaseConstructionStatus = new BaseConstructionStatus

}

object User{

  def apply(reqMsg:ReqMsg): User = {
    val user = new User(reqMsg.getUserId)
    user.constructionStatus = new BaseConstructionStatus
    user
  }
}
