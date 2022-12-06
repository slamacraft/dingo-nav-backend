package com.dingdo.core

import com.dingdo.core.model.mapper.PluginOrderMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class GroupConfigManager {
  GroupConfigManager.instance = this
  @Autowired
  var pluginConfigMapper:PluginOrderMapper = _

}

object GroupConfigManager{
  var instance:GroupConfigManager = _
}