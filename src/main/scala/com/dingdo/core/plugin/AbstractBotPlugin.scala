package com.dingdo.core.plugin

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.repository.MongoRepository

import scala.language.higherKinds

abstract class BasePluginConfig {
  @Id
  var id: String = _
  var name: String = _
  var botId: Long = _
  var groupId: Long = _
}

trait BasePluginConfigMapper[Entity <: BasePluginConfig] extends MongoRepository[Entity, String] {

}

trait BotPluginImpl[Entity <: BasePluginConfig, Mapper <: BasePluginConfigMapper[Entity]] extends BotPlugin {
  @Autowired
  protected var mapper: Mapper = _
}
