package com.dingo.config

import com.dingo.common.JsonMapper
import com.dingo.common.exceptions.BusinessException
import com.dingo.common.util.SpringContextUtil
import com.dingo.config.interceptor.ChannelInterceptor
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.{Autowired, Configurable}
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.servlet.error.{DefaultErrorAttributes, ErrorAttributes}
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.{Bean, Configuration, Lazy, Primary}
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.core.annotation.Order
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestTemplate
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.config.annotation.{InterceptorRegistry, ResourceHandlerRegistry, WebMvcConfigurer}

import java.util

@Primary
@Configuration
class AppConfig extends ApplicationListener[ContextRefreshedEvent] with WebMvcConfigurer {

  @Lazy
  @Autowired
  var channelInterceptor: ChannelInterceptor = _

  override def onApplicationEvent(event: ContextRefreshedEvent): Unit = {
    SpringContextUtil.setApplicationContext(event.getApplicationContext)
  }

  override def addInterceptors(registry: InterceptorRegistry): Unit = {
    registry.addInterceptor(channelInterceptor)
      .addPathPatterns("/**")
      .excludePathPatterns(
        "/error",
        "/swagger-ui.html",
        "/v2/api-docs",
        "/swagger-resources",
        "/swagger-resources/**",
        "/user/login",
        "/null/swagger-resources/**",
        "/webjars/**"
      )
  }

  @Bean
  def objectMapper(): ObjectMapper = JsonMapper.objectMapper


  /**
   * 异常处理器，把{{{BusinessException}}}变为异常信息json
   *
   * @return
   */
  @Bean
  @Order(0x80000000 + 1)
  def errorAttributes(): ErrorAttributes = new DefaultErrorAttributes() {
    override def getErrorAttributes(webRequest: WebRequest, options: ErrorAttributeOptions): util.Map[String, AnyRef] = {
      val errorAttributes = super.getErrorAttributes(webRequest, options)
      errorAttributes.remove("status")
      errorAttributes.remove("error")
      getError(webRequest) match {
        case e: BusinessException =>
          errorAttributes.put("code", e.code)
          errorAttributes.put("message", e.getMessage)
        case _ =>
      }
      errorAttributes
    }

    override def getOrder: Int = Integer.MIN_VALUE + 1
  }

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
