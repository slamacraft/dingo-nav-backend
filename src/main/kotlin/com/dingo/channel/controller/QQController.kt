package com.dingo.channel.controller

import com.dingo.channel.model.ChannelDto
import com.dingo.channel.model.QQModel
import com.dingo.channel.model.VerifyDto
import com.dingo.channel.model.VerifyVo
import com.dingo.channel.service.QQService
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
    private val cacheMsgIds = mutableListOf<String>()
    private val cacheMaxSize = 100

    @PostMapping("/callback")
    fun callback(
        request: HttpServletRequest, @RequestBody dto: QQModel
    ): VerifyVo {
        // 判断消息是否重复
        if(cacheMsgIds.contains(dto.id)){
            // 重复了，直接返回
            return VerifyVo("", "")
        }else{
            // 否则在缓存这个msgId
            if(cacheMsgIds.size >= cacheMaxSize){
                cacheMsgIds.removeAt(cacheMsgIds.size - 1)
            }
            cacheMsgIds.add(0, dto.id)
        }

        val data = dto.d
        when (dto.op) {
            13L -> return qqService.verify(mapToBean(data, VerifyDto::class.java))
            0L -> when (dto.t) {
                "AT_MESSAGE_CREATE" -> qqService.recallChannelAtMsg(mapToBean(data, ChannelDto::class.java))
                "DIRECT_MESSAGE_CREATE" -> qqService.recallChannelPrivateMsg(mapToBean(data, ChannelDto::class.java))
            }
        }
        return VerifyVo("", "")
    }
}

private fun <T> mapToBean(data: Map<String, Any?>, clazz: Class<T>): T {
    val bean = clazz.constructors[0].newInstance() as T
    for ((key, value) in data) {
        try {
            val field = clazz.getDeclaredField(key)
            field.isAccessible = true
            if(value is Map<*, *>){
                field[bean] = mapToBean(value as Map<String, Any?>, field.type)
            }else if(value is Boolean){
                field[bean] = value
            } else{
                field[bean] = field.type.cast(value)
            }
        } catch (e: NoSuchFieldException) {
            // 如果没有这个字段就跳过
        }
    }
    return bean
}