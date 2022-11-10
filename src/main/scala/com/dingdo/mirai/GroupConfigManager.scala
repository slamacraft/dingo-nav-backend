package com.dingdo.mirai

import com.dingdo.model.mapper.{GroupConfigMapper, PluginConfigMapper}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class GroupConfigManager {
  GroupConfigManager.instance = this
  @Autowired
  var groupConfigMapper:GroupConfigMapper = _
  @Autowired
  var pluginConfigMapper:PluginConfigMapper = _



}

object GroupConfigManager{
  var instance:GroupConfigManager = _
}