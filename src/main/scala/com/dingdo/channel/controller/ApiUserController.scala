package com.dingdo.channel.controller

import com.dingdo.channel.model.{ApiLoginReq, ApiLoginResp}
import com.dingdo.channel.service.IApiUserService
import io.swagger.annotations.{Api, ApiOperation}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{PostMapping, RequestBody, RequestMapping, RestController}

@Api("用户控制器")
@RestController("User Controller")
@RequestMapping(Array("/user"))
class ApiUserController {
  @Autowired
  private var apiUserService: IApiUserService = _

  @ApiOperation("登录")
  @PostMapping(Array("/login"))
  def login(@RequestBody req: ApiLoginReq): ApiLoginResp = apiUserService.login(req)

  @ApiOperation("登出")
  @PostMapping(Array("/logout"))
  def logout(): Unit = apiUserService.logout()

}
