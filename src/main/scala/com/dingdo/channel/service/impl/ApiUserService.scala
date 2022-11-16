package com.dingdo.channel.service.impl

import com.dingdo.channel.context.CurrentContext
import com.dingdo.channel.model.{ApiLoginReq, ApiLoginResp}
import com.dingdo.channel.service.IApiUserService
import com.dingdo.common.exceptions.BusinessException
import com.dingdo.component.ICacheContext
import com.dingdo.mirai.{BotManager, MiraiBot}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.util.UUID

@Service
class ApiUserService extends IApiUserService {
  @Autowired
  var cacheContext: ICacheContext = _

  override def login(req: ApiLoginReq): ApiLoginResp = {
    BotManager.getBot(req.id)
      .orElse(Option(MiraiBot(req.id, req.pw).login))
      .filter(_.pw == req.pw)
      .map { it =>
        val token = UUID.randomUUID().toString
        cacheContext.cache(token, it.id)
        val resp = new ApiLoginResp
        resp.id = it.id
        resp.token = token
        resp
      }.getOrElse(throw BusinessException("账号或密码错误"))
  }

  def logout(): Unit = {
    cacheContext.remove[Long](CurrentContext.get.token)
  }

}
