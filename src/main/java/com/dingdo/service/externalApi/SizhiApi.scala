package com.dingdo.service.externalApi

import cn.hutool.core.text.CharSequenceUtil
import cn.hutool.core.util.StrUtil
import com.alibaba.fastjson.JSONObject
import com.dingdo.robot.botDto.factory.BotDtoFactory
import com.dingdo.robot.botDto.{ReplyMsg, ReqMsg}
import com.dingdo.robot.enums.MsgTypeEnum.PRIVATE
import com.dingdo.service.MsgProcessor
import com.dingdo.service.enums.{StrategyEnum, UrlEnum}
import com.dingdo.service.model.MsgFromSiZhi.ChatMsg
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.http.{HttpEntity, HttpHeaders, MediaType}
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

import scala.beans.BeanProperty

/**
 * @author slamacraft 
 * @since JDK 1.8
 * @version 1.0
 */
@Component
@ConfigurationProperties(prefix = "bots.sizhi")
class SizhiApi extends MsgProcessor {

  private val logger = Logger.getLogger(classOf[SizhiApi])

  // 请求的消息
  private val SPOKEN = "spoken"
  private val APP_ID = "appid"
  //自己管理的用户id，填写可进行上下文对话
  private val USERID = "userid"

  @BeanProperty
  var appId: String = _

  @Autowired
  private var restTemplate: RestTemplate = _

  /**
   * 返回封装完毕的回复信息
   *
   * @return
   */
  override def getReply(reqMsg: ReqMsg): ReplyMsg = {
    val msg = reqMsg.getMsg
    val json = new JSONObject

    // 对啥也不说的人的回答
    if (CharSequenceUtil.isBlank(msg)) {
      return if (PRIVATE == reqMsg.getType) BotDtoFactory.replyMsg("你想对我说什么呢？")
      else BotDtoFactory.replyMsg("")
    }

    val headers = new HttpHeaders
    headers.setContentType(MediaType.APPLICATION_JSON)
    json.put(SPOKEN, msg)
    json.put(APP_ID, appId)
    json.put(USERID, reqMsg.getUserId)

    val request = new HttpEntity[JSONObject](json, headers)

    try {
      val response = restTemplate.postForEntity(UrlEnum.SI_ZHI_API.toString, request, classOf[String])
      val api_String = new ObjectMapper().readValue(response.getBody, classOf[ChatMsg])
      return BotDtoFactory.replyMsg(getReplyTextFromResponse(api_String))
    } catch {
      case e: Exception =>
        logger.error(e)
    }

    BotDtoFactory.replyMsg("""
        |不是很懂
        |（；´д｀）ゞ
        |""".stripMargin)
  }


  private def getReplyTextFromResponse(chatMsg: ChatMsg) =
    if (chatMsg.getMessage == "success") chatMsg.getData.getInfo.getText
    else "不是很懂\n" + "（；´д｀）ゞ"


  override def getType: StrategyEnum = StrategyEnum.CHAT
}
