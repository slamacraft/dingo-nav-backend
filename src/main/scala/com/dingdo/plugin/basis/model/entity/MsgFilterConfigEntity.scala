package com.dingdo.plugin.basis.model.entity

import org.springframework.data.annotation.Id

class MsgFilterConfigEntity {
  @Id
  var id: Long = _
  var botId: Long = _
  var groupId: Long = _
  var enable: Boolean = _ // 是否启用该群，如果为false直接屏蔽掉群消息
  var excludeUser: List[Long] = _ // 屏蔽的用户，其发送的消息不会传递给下游插件
  var partten: String = _ // 匹配的正则，不匹配就会不会传递给下游插件
}
