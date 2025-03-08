package com.dingo.channel.controller

import com.dingo.channel.model.ChannelDto
import com.dingo.channel.model.QQModel
import com.dingo.channel.model.VerifyDto
import com.dingo.channel.model.VerifyVo
import com.dingo.channel.service.QQService
import com.dingo.common.collection.ConcurrentFixedQueue
import com.dingo.common.expand.castTo
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/qq")
open class QQController(
    private val qqService: QQService
) {
    // 缓存最近的msgId防止重复回复
    private val cacheMsgIds = ConcurrentFixedQueue<String>(100)

    @PostMapping("/callback")
    fun callback(
        request: HttpServletRequest, @RequestBody dto: QQModel
    ): VerifyVo {
        // 判断消息是否重复
        if (cacheMsgIds.contains(dto.id)) { // 重复了，直接返回
            return VerifyVo("", "")
        } else {
            cacheMsgIds.add(dto.id)
        }

        val data = dto.d
        when (dto.op) {
            13L -> return qqService.verify(data.castTo(VerifyDto::class))
            0L -> when (dto.t) {
                // 频道at消息
                "AT_MESSAGE_CREATE" -> qqService.recallChannelAtMsg(data.castTo(ChannelDto::class))
                // 频道私聊消息
                "DIRECT_MESSAGE_CREATE" -> qqService.recallChannelPrivateMsg(data.castTo(ChannelDto::class))
            }
        }
        return VerifyVo("", "")
    }
}

