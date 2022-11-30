package com.dingdo.core.model.mapper

import com.dingdo.core.model.entity.BotEntity
import org.springframework.data.mongodb.repository.MongoRepository

trait BotMapper extends MongoRepository[BotEntity, Long]
