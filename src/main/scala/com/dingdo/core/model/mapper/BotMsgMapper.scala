package com.dingdo.core.model.mapper

import com.dingdo.core.model.entity.BotMsgEntity
import org.springframework.data.mongodb.repository.MongoRepository

trait BotMsgMapper extends MongoRepository[BotMsgEntity, Long]
