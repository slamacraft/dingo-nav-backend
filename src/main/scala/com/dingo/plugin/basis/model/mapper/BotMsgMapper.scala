package com.dingo.plugin.basis.model.mapper

import com.dingo.plugin.basis.model.entity.BotMsgEntity
import org.springframework.data.mongodb.repository.MongoRepository

trait BotMsgMapper extends MongoRepository[BotMsgEntity, Long]
