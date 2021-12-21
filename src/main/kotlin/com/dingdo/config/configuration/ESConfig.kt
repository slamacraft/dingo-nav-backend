package com.dingdo.config.configuration

import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.json.jackson.JacksonJsonpMapper
import co.elastic.clients.transport.rest_client.RestClientTransport
import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class ESConfig {

//    @Bean
    open fun elasticsearchClient(): ElasticsearchClient {
        val restClient = RestClient.builder(
            HttpHost("localhost", 9200)
        ).build()

        val restClientTransport = RestClientTransport(restClient, JacksonJsonpMapper())
        return ElasticsearchClient(restClientTransport)
    }
}
