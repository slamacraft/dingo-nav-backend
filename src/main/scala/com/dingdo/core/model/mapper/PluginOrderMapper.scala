package com.dingdo.core.model.mapper

import com.dingdo.core.model.entity.PluginOrderEntity
import org.springframework.data.mongodb.repository.MongoRepository

trait PluginOrderMapper extends MongoRepository[PluginOrderEntity, String] {

}
