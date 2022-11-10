package com.dingdo.model.mapper

import com.dingdo.model.entity.PluginConfigEntity
import org.springframework.data.mongodb.repository.MongoRepository

trait PluginConfigMapper extends MongoRepository[PluginConfigEntity,Long]{

}
