package com.dingo.channel.service

import com.dingo.channel.model.{ApiLoginReq, ApiLoginResp}

trait IApiUserService {

  def login(req: ApiLoginReq): ApiLoginResp

  def logout(): Unit

  def stop():Unit
}
