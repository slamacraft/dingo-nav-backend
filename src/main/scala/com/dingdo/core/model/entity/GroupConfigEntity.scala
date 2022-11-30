package com.dingdo.core.model.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("group_config")
class GroupConfigEntity {
  @Id
  var id: Long = _
  var botId: Long = _
  var groupId: Long = _
  var enable:Boolean = _  // 是否启用这个群
  var filterPattern: String = _ // 消息过滤的正则
  var excludeUser: java.util.List[Long] = _ // 排除的使用用户
}
