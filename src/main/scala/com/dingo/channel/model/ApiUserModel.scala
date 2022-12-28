package com.dingo.channel.model

import io.swagger.annotations.{ApiModel, ApiModelProperty}

import scala.beans.BeanProperty

@ApiModel("登录req")
class ApiLoginReq {
  @BeanProperty
  @ApiModelProperty("qq号")
  var id: Long = _
  @BeanProperty
  @ApiModelProperty("密码")
  var pwd: String = _
}

class ApiLoginResp {
  @BeanProperty
  var id: Long = _
  @BeanProperty
  var token: String = _
}
