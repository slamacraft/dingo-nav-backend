package com.dingo.channel.controller

import com.dingo.channel.model.BaseMessageVo
import com.dingo.channel.model.BaseMsgDto.Companion.toDto
import com.dingo.channel.model.GroupMsgDto
import com.dingo.channel.model.PrivateMsgDto
import com.dingo.channel.service.oneBot.OneBotMsgService
import com.dingo.common.expand.caseTo
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/oneBot")
class OneBotController(
    private val objectMapper: ObjectMapper,
    private val oneBotMsgService: OneBotMsgService
) {

    @PostMapping("/callback")
    fun callback(request: HttpServletRequest): BaseMessageVo? {
        // 获取request的body并转化为JsonNode
        val body = request.inputStream.bufferedReader()
            .use { it.readText() }
        val jsonNode = objectMapper.readTree(body)

        val resp =  when (jsonNode["post_type"].asText()) {
            "message" -> {
                when (jsonNode["message_type"].asText()) {
                    "private" -> oneBotMsgService.handlePrivateMsg(jsonNode.toDto(PrivateMsgDto::class))
                    "group" -> oneBotMsgService.handleGroupMsg(jsonNode.toDto(GroupMsgDto::class))
                    else -> throw RuntimeException("无效的消息类型${jsonNode["message_type"].asText()}")
                }
            }
            else -> null
        }

        return resp
    }

}