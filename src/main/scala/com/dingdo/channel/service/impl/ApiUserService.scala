package com.dingdo.channel.service.impl

import com.dingdo.channel.model.{ApiLoginReq, ApiLoginResp}
import com.dingdo.channel.service.IApiUserService
import com.dingdo.common.exceptions.BusinessException
import com.dingdo.mirai.{BotManager, MiraiBot}
import org.springframework.stereotype.Service

@Service
class ApiUserService extends IApiUserService {

  override def login(req: ApiLoginReq): ApiLoginResp = {
    BotManager.getBot(req.id)
      .orElse(Option(MiraiBot(req.id, req.pw).login))
      .filter(_.pw == req.pw)
      .map{it=>

      }.orElse(throw BusinessException("账号或密码错误"))
    return null
  }

  def logout(): Unit = {

  }

}
