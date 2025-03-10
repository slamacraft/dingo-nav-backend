package com.dingo.common.expand

import cn.hutool.core.io.IoUtil
import com.dingo.common.ObjectMappers
import com.dingo.config.AppConfig
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import kotlin.reflect.KClass


fun Request.Builder.post(body: Any): Request.Builder {
    val bodyJson = ObjectMappers.jsonMapper.writeValueAsString(body)
    return header("Content-Type", "application/json")
        .post(bodyJson.toRequestBody())
}

fun Request.sendRequest(): Response {
    val response =  AppConfig.okHttpClient.newCall(this).execute()
    println("""
        请求: ${this.url}

        ${ IoUtil.read(response.body!!.byteStream(), Charsets.UTF_8)}
    """.trimIndent())
    return response
}

fun <T :Any> Request.sendRequestThenGetResp(clz: KClass<T>): T {
    val response = AppConfig.okHttpClient.newCall(this).execute()
    val bodyStr = IoUtil.read(response.body!!.byteStream(), Charsets.UTF_8)
    println("""
        请求: ${this.url}

        $bodyStr
    """.trimIndent())
    return ObjectMappers.jsonMapper.readValue(bodyStr, clz.java)
}