package com.dingdo.robot.miraiScala

import com.dingdo.robot.botDto.factory.BotDtoFactory
import com.dingdo.robot.botService.{GroupMsgService, PrivateMsgService}
import com.dingdo.robot.mirai.MiraiRobot
import com.dingdo.service.base.RepeatComponent
import net.mamoe.mirai.Bot
import net.mamoe.mirai.event.events.{FriendMessageEvent, GroupMessageEvent}
import net.mamoe.mirai.message.data.{At, SingleMessage}
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

import java.util
import javax.annotation.PostConstruct
import scala.collection.JavaConverters._


//@Component
//@ConfigurationProperties(prefix = "bots.core")
class MiraiTest {

  private val logger = Logger.getLogger(classOf[MiraiRobot])

  @Autowired private var privateMsgService: PrivateMsgService = _

  @Autowired private var groupMsgService: GroupMsgService = _

  @Autowired private var repeatComponent: RepeatComponent = _

  private var loginInfo = List[String]()

  @PostConstruct
  @throws[InterruptedException]
  def run(): Unit = {
    val botUserPwInfo = getBotUserPwInfo
    MiraiRobotLander.robotLogin(botUserPwInfo)
    MiraiRobotLander.registeredFriendMsgEvent(this.privateEvent)
    MiraiRobotLander.registeredGroupMsgEvent(this.groupEvent)
  }


  def groupEvent(event: GroupMessageEvent): Unit = {
    val message = event.getMessage
    val receive = BotDtoFactory.reqMsg(event)

    repeatComponent.repeat(receive)

    val atBotOption = message.stream
      .filter((item: SingleMessage) => item.isInstanceOf[At] && item.asInstanceOf[At].getTarget == event.getBot.getId)
      .map((item: SingleMessage) => item.asInstanceOf[At])
      .findFirst

    if (atBotOption.isPresent) {
      val replyMsg = groupMsgService.handleMsg(receive)
      event.getGroup.sendMessage(replyMsg.getReplyMsg)
    }
  }


  def privateEvent(event: FriendMessageEvent): Unit = {
    val receive = BotDtoFactory.reqMsg(event)
    val replyMsg = privateMsgService.handleMsg(receive)
    event.getFriend.sendMessage(replyMsg.getReplyMsg)
  }


  /**
   * 从启动后加载的配置文件中加载登录机器人的账号密码
   *
   * @return
   */
  private def getBotUserPwInfo = {
    loginInfo.map(botInfo => botInfo.split(":"))
      .filter(idAndPw => idAndPw.length == 2)
      .map(idAndPw => (idAndPw(0).toLong, idAndPw(1)))
      .toMap
  }

  def getBotInfo(userId: Long): Bot = MiraiRobotLander.getBotInfo(userId).get

  def getBotList: util.List[Bot] = MiraiRobotLander.getBotList

  def getLoginInfo: util.List[String] = loginInfo.asJava

  def setLoginInfo(loginInfo: List[String]): Unit = this.loginInfo = loginInfo

}
