package com.dingdo.core

import com.dingdo.core.mirai.OneMsg
import com.dingdo.core.model.entity.PluginOrderEntity
import com.dingdo.core.model.mapper.PluginOrderMapper
import net.mamoe.mirai.event.events.MessageEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.{ApplicationContext, ApplicationContextAware}
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.{Criteria, Query, Update}
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import java.util.concurrent.ConcurrentLinkedQueue
import java.util.stream.Collectors
import scala.collection.mutable
import scala.language.postfixOps


@Component
class BotPluginHandler extends ApplicationContextAware {
  private val pluginList = new mutable.MutableList[PluginConfig]
  @Autowired
  private var pluginOrderMapper: PluginOrderMapper = _
  @Autowired
  private var mongoTemplate: MongoTemplate = _
  // 初始化伴生对象
  BotPluginHandler.INSTANCE = this

  class PluginConfig(val plugin: BotPlugin, val config: PluginOrderEntity) {
    val childList = new ConcurrentLinkedQueue[BotPlugin]
  }

  override def setApplicationContext(applicationContext: ApplicationContext): Unit = {
    val plugins = applicationContext.getBeansOfType(classOf[BotPlugin]).values()
    val pluginConfigEntities = pluginOrderMapper.findAll()

    // 检查是否有重复名称的插件
    plugins.stream().collect(Collectors.groupingBy[BotPlugin, String](_.name))

    val parentNameMap = pluginConfigEntities.stream()
      .collect(Collectors.groupingBy[PluginOrderEntity, String](_.name))

    // 初始化列表
    val pluginList = plugins.stream().flatMap { it =>
      pluginConfigEntities.stream()
        .filter(config => it.name == config)
        .map(config => new PluginConfig(it, config))
    }.collect(Collectors.toList[PluginConfig])

    pluginList.forEach { it =>
      Option(parentNameMap.get(it.plugin.name)).map { configList =>
        configList.stream().flatMap { config =>
          plugins.stream().filter { plugin => plugin.name == config.name }
        }.collect(Collectors.toList[BotPlugin])
      }.foreach { childPlugin =>
        it.childList.addAll(childPlugin)
      }
    }
  }


  def handle(msg: MessageEvent): Unit = {

    def handler_2(inputMsg: OneMsg, pluginConfig: PluginConfig): Unit = pluginConfig.plugin(inputMsg) match {
      case handleResult: OneMsg =>
        pluginConfig.childList.forEach { it =>
          handler_2(handleResult, pluginList.filter(_.plugin == it).head)
        }
      case _ =>
    }

    for (elem <- pluginList.filter(it => it.config.parentName == BotPlugin.name)) {
      handler_2(msg, elem)
    }
  }


  @Transactional
  def updatePluginOrderBatch(updateList: mutable.Seq[(String, String, Boolean)]): Unit = {
    for ((name, parentName, enable) <- updateList) {
      updatePluginOrder(name, parentName, enable)
    }
  }


  private def updatePluginOrder(name: String, parentName: String, enable: Boolean = true): Unit = {
    pluginList.find(_.plugin.name == name).foreach { pluginConfig =>
      // 从父插件的子集中移除本插件
      val config = pluginConfig.config
      pluginList.find(_.plugin.name == config.parentName)
        .foreach(_.childList.remove(pluginConfig))
      // 将自己插入到新的父插件的子集中
      pluginList.find(_.plugin.name == parentName)
        .foreach(_.childList.add(pluginConfig.plugin))
      // 修改自身配置
      config.parentName = parentName
      config.enable = enable
    }

    // 更新mongoDB
    mongoTemplate.updateFirst(
      Query.query(Criteria.where("name").is(name)),
      Update.update("parentName", parentName).set("enable", enable),
      classOf[PluginOrderEntity]
    )
  }

}

object BotPluginHandler {
  var INSTANCE: BotPluginHandler = _
}


