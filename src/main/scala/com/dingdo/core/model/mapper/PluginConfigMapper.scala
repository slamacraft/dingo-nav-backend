package com.dingdo.core.model.mapper

import com.dingdo.core.model.entity.PluginConfigEntity
import org.springframework.data.mongodb.repository.MongoRepository

trait PluginConfigMapper extends MongoRepository[PluginConfigEntity, Long] {

}
