package com.dingdo.core.model.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
class PluginConfigEntity {
  @Id
  var id: Long = _
  var botId: Long = _
  var groupId: Long = _
  var plugin:String = _
}
