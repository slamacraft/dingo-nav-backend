package com.dingo.core.model.entity

import com.dingo.core.BotPlugin
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("pluginOrder")
class PluginOrderEntity {
  @Id
  var name: String = _
  var parentName: String = _
  var enable: Boolean = _
}

object PluginOrderEntity {
  def defaultEntity(plugin: BotPlugin): PluginOrderEntity = {
    val entity = new PluginOrderEntity()
    entity.name = plugin.name()
    entity.parentName = "ROOT"
    entity.enable = true
    entity
  }
}