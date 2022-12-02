package com.dingdo.core.model.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("pluginOrder")
class PluginOrderEntity {
  @Id
  var name: String = _
  var parentName: String = _
  var enable: Boolean = _
}
