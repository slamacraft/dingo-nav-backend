package com.dingdo.config.configuration

import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestTemplate
import org.springframework.web.servlet.config.annotation.{ResourceHandlerRegistry, WebMvcConfigurer}

@Configuration
class WebConfig extends WebMvcConfigurer {

  override def addResourceHandlers(registry: ResourceHandlerRegistry): Unit = {
    registry.addResourceHandler("/**").addResourceLocations("classpath:/static/")
    registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/")
    registry.addResourceHandler("/webjar/**").addResourceLocations("classpath:/META-INF/resources/webjars/")
    //        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
    //        registry.addResourceHandler("/**").addResourceLocations("file:C://Users/Administrator/Desktop/");
  }

  @Bean def restTemplate: RestTemplate = {
    val requestFactory = new SimpleClientHttpRequestFactory
    //        requestFactory.setConnectTimeout(10 * 1000);// 设置超时
    //        requestFactory.setReadTimeout(10 * 1000);
    new RestTemplate(requestFactory)
  }
}
