package com.dingdo.model.mapper

import com.dingdo.model.entity.BotEntity
import org.springframework.data.mongodb.repository.MongoRepository


trait BotMapper extends MongoRepository[BotEntity, Long]
