package com.dingo.channel.controller

import com.dingo.channel.model.{ApiLoginReq, ApiLoginResp}
import com.dingo.channel.service.IApiUserService
import io.swagger.annotations.{Api, ApiOperation}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{PostMapping, RequestBody, RequestMapping, RestController}

@Api("用户控制器")
@RestController
@RequestMapping(value = Array("/user"))
class ApiUserController {
  @Autowired
  private var apiUserService: IApiUserService = _

  @ApiOperation("登录")
  @PostMapping(value =  Array("/login"))
  def login(@RequestBody req: ApiLoginReq): ApiLoginResp = apiUserService.login(req)

  @ApiOperation("登出")
  @PostMapping(value =  Array("/logout"))
  def logout(): Unit = apiUserService.logout()

  @ApiOperation("机器人下线")
  @PostMapping(value =  Array("/stop"))
  def stop(): Unit = apiUserService.stop()
}
