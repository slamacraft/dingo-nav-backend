package com.dingo.core.qq.util

import com.dingo.common.expand.post
import com.dingo.core.qq.QQAuthHandler
import okhttp3.Request

/**
 * 发送消息的基础地址
 */
private const val baseUrl = "https://api.sgroup.qq.com"

fun String.requestBuilder(): Request.Builder {
    return Request.Builder()
        .url("$baseUrl$this")
        .addHeader("Authorization", "QQBot ${QQAuthHandler.instance.getAccessToken()}")
}

/**
 * 将String视为一个url地址构建请求
 */
fun String.postRequest(body: Any): Request {
    return Request.Builder()
        .url("$baseUrl$this")
        .addHeader("Authorization", "QQBot ${QQAuthHandler.instance.getAccessToken()}")
        .post(body)
        .build()
}