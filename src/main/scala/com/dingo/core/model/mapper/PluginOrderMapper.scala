package com.dingo.core.model.mapper

import com.dingo.core.model.entity.PluginOrderEntity
import org.springframework.data.mongodb.repository.MongoRepository

trait PluginOrderMapper extends MongoRepository[PluginOrderEntity, String] {

}
