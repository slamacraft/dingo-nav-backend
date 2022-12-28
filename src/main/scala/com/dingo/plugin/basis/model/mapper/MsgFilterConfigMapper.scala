package com.dingo.plugin.basis.model.mapper

import com.dingo.plugin.basis.model.entity.MsgFilterConfigEntity
import org.springframework.data.mongodb.repository.MongoRepository


trait MsgFilterConfigMapper extends MongoRepository[MsgFilterConfigEntity, Long] {
  def findFirstByBotId(botId: Long):MsgFilterConfigEntity

}
