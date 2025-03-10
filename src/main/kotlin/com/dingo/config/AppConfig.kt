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
import kotlin.reflect.KClass

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
