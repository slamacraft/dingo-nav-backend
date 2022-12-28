package com.dingo.channel.service.impl

import com.dingo.channel.context.CurrentContext
import com.dingo.channel.model.{ApiLoginReq, ApiLoginResp}
import com.dingo.channel.service.IApiUserService
import com.dingo.common.exceptions.BusinessException
import com.dingo.component.ICacheContext
import com.dingo.core.mirai.{BotManager, MiraiBot}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.util.UUID

@Service
class ApiUserService extends IApiUserService {
  @Autowired
  var cacheContext: ICacheContext = _

  override def login(req: ApiLoginReq): ApiLoginResp = BotManager.getBot(req.id)
    .orElse(Option(MiraiBot(req.id, req.pwd).login))
    .filter(_.pw == req.pwd)
    .map { it =>
      val token = UUID.randomUUID().toString
      cacheContext.cache(token, it.id)
      val resp = new ApiLoginResp
      resp.id = it.id
      resp.token = token
      resp
    }.getOrElse(throw BusinessException("账号或密码错误"))

  override def logout(): Unit = {
    cacheContext.remove[Long](CurrentContext.get.token)
  }

  override def stop():Unit = {
    BotManager.getBot(CurrentContext.get.id).foreach(_.bot.close())
  }
}
