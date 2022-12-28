package com.dingo.core

import com.dingo.common.PackageScanner
import com.dingo.core.model.entity.PluginOrderEntity
import com.dingo.core.model.mapper.PluginOrderMapper
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.events.{FriendMessageEvent, GroupMessageEvent, MessageEvent}
import net.mamoe.mirai.message.data.FileMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.{ApplicationContext, ApplicationContextAware}
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.{Criteria, Query, Update}
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import java.io.{File, FileOutputStream}
import java.net.{URL, URLClassLoader}
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.stream.Collectors
import scala.collection.mutable
import scala.io.Source
import scala.language.postfixOps
import scala.jdk.CollectionConverters.*
import BotPluginManager.:?

import scala.annotation.targetName

@Component
class BotPluginManager extends ApplicationContextAware {
  private val pluginList = new mutable.ArrayBuffer[PluginConfig]
  @Autowired
  private var pluginOrderMapper: PluginOrderMapper = _
  @Autowired
  private var mongoTemplate: MongoTemplate = _
  // 初始化伴生对象
  BotPluginManager.INSTANCE = this

  
  class PluginConfig(val plugin: BotPlugin, val config: PluginOrderEntity) {
    val childList = new ConcurrentLinkedQueue[BotPlugin]
  }
  
  object PluginConfig {
    def apply(plugin: BotPlugin)(using allConfigEntities: java.util.List[PluginOrderEntity]): PluginConfig = {
      val config = allConfigEntities.stream()
        .filter(config => plugin.name == config.name)
        .findFirst()
        .orElseGet { () =>
          val entity = PluginOrderEntity.defaultEntity(plugin)
          pluginOrderMapper.save(entity)
          entity
        }
      new PluginConfig(plugin, config)
    }
  }

  
  override def setApplicationContext(applicationContext: ApplicationContext): Unit = {
    val plugins = applicationContext.getBeansOfType(classOf[BotPlugin]).values()

    // 所有的插件顺序配置
    given pluginConfigEntities: java.util.List[PluginOrderEntity] = pluginOrderMapper.findAll()
    // 检查是否有重复名称的插件
    plugins.stream().collect(Collectors.groupingBy[BotPlugin, String](_.name))
    // 初始化列表
    plugins.forEach(it => pluginList += PluginConfig(it))

    val parentNameMap = pluginList.groupMap(_.config.parentName)(_.plugin.name())

    pluginList.foreach { it =>
      parentNameMap.get(it.plugin.name()).map { childNameList =>
        childNameList.flatMap { childName =>
          pluginList.filter { plugin => plugin.plugin.name() == childName }
        }
      }.foreach { childPlugins =>
        it.childList.addAll(childPlugins.map(_.plugin).asJava)
      }
    }
  }


  def handle(msg: GroupMessageEvent): Unit = {

    def handleRecursion(inputMsg: OneMsg, pluginConfig: PluginConfig): Unit =
      pluginConfig.plugin(inputMsg) match {
        case handleResult: OneMsg =>
          pluginConfig.childList.forEach { it =>
            handleRecursion(handleResult, pluginList.filter(_.plugin == it).head)
          }
        case _ =>
      }

    for (elem <- pluginList.filter(it => it.config.parentName == "ROOT")) {
      handleRecursion(OneMsg(msg), elem)
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
      config.parentName = parentName :? "ROOT"
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


object BotPluginManager {
  var INSTANCE: BotPluginManager = _

  extension[T] (t: T) {
    @targetName("getOrElse")
    def :?(defaultValue: => T): T = {
      if (t != null) {
        t
      } else {
        defaultValue
      }
    }
  }
}


