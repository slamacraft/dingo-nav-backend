package com.dingdo.componets.conrtruction.base

import com.dingdo.componets.conrtruction.Construction
import com.dingdo.componets.conrtruction.manager.user.User
import com.dingdo.robot.botDto.ReqMsg
import org.springframework.context.{ApplicationContext, ApplicationContextAware}
import org.springframework.stereotype.Component

import scala.collection.JavaConverters._
import scala.collection.mutable

@Component
class Constructor extends ApplicationContextAware {

  var constructionMap: mutable.Map[String, Construction] = _

  var userStatusMap: mutable.Map[String, User] = mutable.HashMap[String, User]()

  override def setApplicationContext(applicationContext: ApplicationContext): Unit = {
    val constructionBeans = applicationContext.getBeansOfType(classOf[Construction])
    constructionMap = for (it <- constructionBeans.asScala) yield it._2.value -> it._2
  }


  def executor(reqMsg: ReqMsg): Option[ConstructionReply] = {
    implicit val user: User = userStatusMap.getOrElseUpdate(reqMsg.getSelfId, User(reqMsg))

    // 如果用户状态已被锁定，那么直接进入状态指定的指令逻辑中
    if (user.constructionStatus.lockFlag) {
      val construction = constructionMap(user.constructionStatus.status)
      implicit val result: ConstructionReply = construction.run(reqMsg, user)
      Option(postProcess(construction, reqMsg))
    } else {
      constructionMap.get(reqMsg.getMsg.split(" ")(0))
        .filter(_.check(reqMsg, user))
        .map(runConstruction(_, reqMsg))
    }
  }


  /**
   * 根据用户的状态，是否锁定来确定是否执行指令的前置处理
   * 执行完成后，根据执行的结果确认是否要执行指令的后置处理
   * @param construction  指令bean
   * @param reqMsg  请求
   * @param user  用户
   * @return
   */
  def runConstruction(construction: Construction, reqMsg: ReqMsg)
                     (implicit user: User): ConstructionReply = {
    implicit val result: ConstructionReply = preProcess(construction, reqMsg).run(reqMsg, user)
    postProcess(construction, reqMsg)
  }


  /**
   * 如果用户没有执行前置处理，则执行前置处理
   *
   * @param construction  指令bean
   * @param reqMsg  请求
   * @param user  请求用户
   * @return
   */
  def preProcess(construction: Construction, reqMsg: ReqMsg)
                (implicit user: User): Construction = {
    if (construction.value.ne(user.constructionStatus.status)) {
      construction.preOperate(reqMsg, user)
    }
    construction
  }


  /**
   * 指令运行完后，判断是不是指令执行完毕，如果执行完毕则执行后置处理
   *
   * @param construction  指令bean
   * @param reqMsg  请求
   * @param user  请求用户
   * @param result 指令执行结果
   * @return
   */
  def postProcess(construction: Construction, reqMsg: ReqMsg)
                 (implicit user: User, result: ConstructionReply): ConstructionReply = {
    if (result.isSuccess) {
      construction.postOperate(reqMsg, user)
    }
    result
  }


}
