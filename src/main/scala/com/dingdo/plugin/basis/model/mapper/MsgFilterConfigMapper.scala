package com.dingdo.plugin.basis.model.mapper

import com.dingdo.plugin.basis.model.entity.MsgFilterConfigEntity
import org.springframework.data.mongodb.repository.MongoRepository


trait MsgFilterConfigMapper extends MongoRepository[MsgFilterConfigEntity, Long] {
  def findFirstByBotId(botId: Long):MsgFilterConfigEntity

}
