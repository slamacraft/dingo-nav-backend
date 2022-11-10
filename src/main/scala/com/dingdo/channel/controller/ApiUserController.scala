package com.dingdo.channel.controller

import com.dingdo.channel.model.{ApiLoginReq, ApiLoginResp}
import com.dingdo.channel.service.IApiUserService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping

@Controller("/user")
class ApiUserController {
  private var apiUserService:IApiUserService = _

  @PostMapping(Array("/login"))
  def login(req: ApiLoginReq): ApiLoginResp = {
    apiUserService.login(req)
  }

}
