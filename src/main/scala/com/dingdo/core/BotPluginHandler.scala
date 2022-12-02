package com.dingdo.core

import com.dingdo.core.mirai.{BotMsg, OneMsg}
import com.dingdo.core.model.entity.PluginOrderEntity
import com.dingdo.core.model.mapper.PluginOrderMapper
import com.dingdo.core.plugin.BotPlugin
import net.mamoe.mirai.event.events.MessageEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.{ApplicationContext, ApplicationContextAware}
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.{Criteria, Query, Update}
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import java.util
import java.util.stream.Collectors
import scala.collection.mutable
import scala.collection.JavaConverters._
import scala.language.postfixOps


@Component
class BotPluginHandler extends ApplicationContextAware {
  private val pluginChildMap = new mutable.HashMap[String, mutable.Buffer[BotPlugin]] // 正查集
  //  private val pluginParentMap = new mutable.HashMap[BotPlugin, String] // 反查集
  private val pluginList = new mutable.MutableList[BotPlugin]
  @Autowired
  private var pluginOrderMapper: PluginOrderMapper = _
  @Autowired
  private var mongoTemplate: MongoTemplate = _

  override def setApplicationContext(applicationContext: ApplicationContext): Unit = {
    val plugins = applicationContext.getBeansOfType(classOf[BotPlugin]).values()
    pluginList ++= plugins.asScala

    val parentNameMap = pluginOrderMapper.findAll().asScala.groupBy(_.parentName)

    // 检查是否有重复名称的插件
    val pluginNameMap = plugins.stream()
      .collect(Collectors.groupingBy[BotPlugin, String](_.name))

    for {(parentName, pluginOrder) <- parentNameMap} {
      val childNames = pluginOrder.map(_.name)
      pluginChildMap += parentName -> pluginList.filter(it => childNames.contains(it.name)).toBuffer
    }
  }

  def handle(msg: MessageEvent): Unit = {
    val msgHandleStack = new util.Stack[(BotMsg, BotPlugin)]
    msgHandleStack.push(msg, BotPlugin)

    while (!msgHandleStack.isEmpty) {
      val (msg, plugin) = msgHandleStack.pop()
      val handleResult = plugin(msg)

      handleResult match {
        case msg: OneMsg =>
          val childList = pluginChildMap(plugin.name)
          for (it <- childList reverse) {
            msgHandleStack.add((msg, it))
          }
        case _ =>
      }
    }
  }

  @Transactional
  def updatePluginOrderBatch(updateList: mutable.Seq[(String, String)]): Unit = {
    for ((name, parentName) <- updateList) {
      updatePluginOrder(name, parentName)
    }
  }

  private def updatePluginOrder(name: String, parentName: String): Unit = {
    pluginList.find(_.name == name).foreach { plugin =>
      for ((_, plugins) <- pluginChildMap) {
        plugins -= plugin
      }
      pluginChildMap(parentName) += plugin
    }

    // 更新mongoDB
    mongoTemplate.updateFirst(
      Query.query(Criteria.where("name").is(name)),
      Update.update("parentName", parentName),
      classOf[PluginOrderEntity]
    )
  }

}
