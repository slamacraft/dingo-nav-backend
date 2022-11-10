package com.dingdo.channel.service.impl

import com.dingdo.channel.model.{ApiLoginReq, ApiLoginResp}
import com.dingdo.channel.service.IApiUserService
import org.springframework.stereotype.Service

@Service
class ApiUserService extends IApiUserService {

  override def login(req: ApiLoginReq): ApiLoginResp = {

    return null
  }

}
