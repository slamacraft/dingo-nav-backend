package com.dingdo.service.externalApi

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
import org.springframework.http.{HttpEntity, HttpHeaders, MediaType}
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

/**
 * @date 2021/1/4 19:12
 * @author slamacraft 
 * @since JDK 1.8
 * @version 1.0
 */
@Component
class SizhiApi extends MsgProcessor {

  private val logger = Logger.getLogger(classOf[SizhiApi])

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
    if (StrUtil.isBlank(msg)) {
      return if (PRIVATE == reqMsg.getType) BotDtoFactory.replyMsg("你想对我说什么呢？")
      else BotDtoFactory.replyMsg("")
    }

    val headers = new HttpHeaders
    headers.setContentType(MediaType.APPLICATION_JSON_UTF8)
    json.put("spoken", msg) //请求的文本
    json.put("appid", "cebaf94c551f180d5c6847cf1ccaa1fa") // 先使用统一的机器人pid
    json.put("userid", reqMsg.getUserId) //自己管理的用户id，填写可进行上下文对话

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
