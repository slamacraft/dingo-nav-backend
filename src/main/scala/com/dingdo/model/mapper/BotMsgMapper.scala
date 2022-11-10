package com.dingdo.model.mapper

import com.dingdo.model.entity.BotMsgEntity
import org.springframework.data.mongodb.repository.MongoRepository


trait BotMsgMapper extends MongoRepository[BotMsgEntity, Long]