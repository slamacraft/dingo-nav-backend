package com.dingdo.plugin.basis.model.entity

import org.springframework.data.annotation.Id

class MsgFilterConfigEntity {
  @Id
  var id: Long = _
  var botId: Long = _
  var partten: String = _ // 匹配的正则，不匹配就会过滤
}
