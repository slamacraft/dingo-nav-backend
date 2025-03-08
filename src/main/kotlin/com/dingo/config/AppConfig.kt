package com.dingo.config


import cn.hutool.core.io.IoUtil
import com.dingo.common.ObjectMappers
import okhttp3.OkHttpClient
import okhttp3.OkHttpClient.Builder
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.springframework.beans.factory.annotation.Configurable
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configurable
@Configuration
open class AppConfig {

    companion object {
        lateinit var okHttpClient: OkHttpClient
    }

    @Bean
    open fun okHttpClient(): OkHttpClient {
        okHttpClient = Builder()
            .connectTimeout(Duration.ofSeconds(100))
            .readTimeout(Duration.ofSeconds(100))
            .build()
        return okHttpClient
    }
}

fun Request.Builder.post(body: Any): Request.Builder {
    val bodyJson = ObjectMappers.jsonMapper.writeValueAsString(body)
    return header("Content-Type", "application/json")
        .post(bodyJson.toRequestBody())
}

fun Request.sendRequest(): Response {
    return AppConfig.okHttpClient.newCall(this).execute()
}

fun <T> Request.sendRequestThenGetResp(clz: Class<T>): T {
    val response = AppConfig.okHttpClient.newCall(this).execute()
    val bodyStr = IoUtil.read(response.body!!.byteStream(), Charsets.UTF_8)
    println("""
        请求: ${this.url}

        $bodyStr
    """.trimIndent())
    return ObjectMappers.jsonMapper.readValue(bodyStr, clz)
}