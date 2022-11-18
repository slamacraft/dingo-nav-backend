package com.dingdo.channel.service

import com.dingdo.channel.model.{ApiLoginReq, ApiLoginResp}

trait IApiUserService {

  def login(req: ApiLoginReq): ApiLoginResp

  def logout(): Unit
}
