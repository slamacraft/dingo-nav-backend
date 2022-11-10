package com.dingdo.model.mapper

import com.dingdo.model.entity.GroupConfigEntity
import org.springframework.data.mongodb.repository.MongoRepository

trait GroupConfigMapper extends MongoRepository[GroupConfigEntity, Long] {

}
