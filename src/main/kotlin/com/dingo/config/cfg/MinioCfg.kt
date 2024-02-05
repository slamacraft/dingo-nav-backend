package com.dingo.config.cfg

import com.dingo.config.properties.MinioProperty
import io.minio.MinioClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class MinioCfg {
    companion object{
        lateinit var client: MinioClient
    }

    @Bean
    open fun getMinioClient(property: MinioProperty): MinioClient {
        client = MinioClient.builder()
            .endpoint(property.url)
            .credentials(property.key, property.secret)
            .build()
        return client
    }

}