package com.dingdo.componets.conrtruction.cmd.factory

import com.dingdo.componets.conrtruction.base.ConstructionReply
import com.dingdo.componets.conrtruction.cmd.CmdCommand
import com.dingdo.componets.conrtruction.manager.user.User
import com.dingdo.robot.botDto.ReqMsg
import com.dingdo.robot.botDto.factory.BotDtoFactory
import com.dingdo.service.base.DefaultResponder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.{ApplicationContext, ApplicationContextAware}
import org.springframework.stereotype.Component

import scala.collection.JavaConverters._
import scala.collection.mutable

@Component
class CommandComponent extends ApplicationContextAware {

  var commandMap: mutable.Map[String, CmdCommand] = _

  @Autowired
  var defaultResponder: DefaultResponder = _


  def executeCommand(reqMsg: ReqMsg, user: User): ConstructionReply = {
    val command = reqMsg.getMsg.split(" ")(0)
    val commandRunner = commandMap.get(command)

    def errorCommand: ConstructionReply = ConstructionReply.suspend(
      BotDtoFactory.replyMsg(s"$command 不是内部或外部命令，也不是可运行的程序或批处理文件")
      , defaultResponder.defaultReply(reqMsg, _)
    )

    commandRunner.fold(errorCommand)(runner => {
      runner.check(reqMsg, user)
      runner.run(reqMsg, user)
    })
  }

  override def setApplicationContext(applicationContext: ApplicationContext): Unit = {
    val commandBeans = applicationContext.getBeansOfType(classOf[CmdCommand])
    commandMap = for (it <- commandBeans.asScala) yield it._2.command() -> it._2
  }
}
