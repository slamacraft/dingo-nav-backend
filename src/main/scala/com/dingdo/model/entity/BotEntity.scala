package com.dingdo.model.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository

@Document("bot")
class BotEntity{
  @Id
  var id: Long = _
  var pw:String = _
  var name:String = _
}
